package org.spring.springboot.controller;

import org.spring.springboot.dao.DatasetMapper;
import org.spring.springboot.dao.ModelMapper;
import org.spring.springboot.dao.RecordMapper;
import org.spring.springboot.domain.Dataset;
import org.spring.springboot.domain.Record;
import org.spring.springboot.domain.model;
import org.spring.springboot.test.SimpleSvm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller

@RequestMapping(value = "/")
public class AlgorithmController {
    @Autowired
    private ModelMapper modeldao;

    @Autowired
    private RecordMapper recorddao;

    @ResponseBody
    @RequestMapping(value = "train")
    public Map train(HttpServletRequest request, HttpServletResponse response, @RequestBody Map data
    ) throws IOException {
        Map resultmap = new HashMap<>();//返回结果
        String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");
        String filepath = rootPath + (String) data.get("owner") + File.separator + (String) data.get("dataset");
        String modelpath = rootPath + (String) data.get("owner") + File.separator + "model" + File.separator + (String) data.get("model");
        ArrayList<ArrayList<String>> dataall = readTable(filepath);//找到数据集路线

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        Map temp = new HashMap();
        temp.put("owner", data.get("owner"));
        temp.put("model", data.get("model"));
        List<model> model = modeldao.selectOne(temp);
        Double cost = 0.0;
        if ("Ridge".equals(model.get(0).getType())) {

            //resultmap.put("type", "当前分析方法为SVM分类分析法");

            dataall.remove(0);

            ArrayList<ArrayList<String>> trainall = new ArrayList<>();

            Set<String> typeall = SimpleSvm.huoqutype(dataall);//获取标签，在最后一列

            for (int i = 0; i < dataall.size(); i++) {

                ArrayList<String> chuliitem = dataall.get(i);//挨个处理

                String chulitype = chuliitem.get(chuliitem.size() - 1);//type

                int k = 0;
                for (String dtype : typeall) {

                    if (dtype.equals(chulitype)) {

                        if (k == 0) {
                            chuliitem.set(chuliitem.size() - 1, "-1");
                        } else {
                            chuliitem.set(chuliitem.size() - 1, "1");
                        }

                    }

                    k++;

                }
            }

            for (int i = 0; i < Math.floor(dataall.size() / 3); i++) {

                trainall.add(dataall.get(i));
            }

            // 调用 训练集和测试集进行对比测试

            SimpleSvm svm = new SimpleSvm(0.0001, (Double) data.get("step"), (String) data.get("model"), modelpath, model.get(0).getCost());

            Integer xlength = dataall.get(0).size();
            Integer ylength = dataall.size();
            Integer train_ylength = trainall.size();

            double[] y = new double[train_ylength];
            double[][] X = new double[train_ylength][xlength];
            svm.loadAarray(X, y, trainall);

            cost = svm.Train(X, y, Integer.parseInt((String) data.get("counts")));

            double[] test_y = new double[ylength];
            double[][] test_X = new double[ylength][xlength];
            svm.loadAarray(test_X, test_y, dataall);
            // svm.Test(test_X, test_y);

            for (int i = 0; i < test_X.length; i++) {
                ArrayList<String> datamodel = dataall.get(i);

                datamodel.add(String.valueOf(svm.predict(test_X[i])));

                String flag = "";
                String res = datamodel.get(datamodel.size() - 1);
                String orgres = datamodel.get(datamodel.size() - 2);
                if (res.equals(orgres)) {
                    flag = "预测正确";
                } else {
                    flag = "预测错误";
                }

                if ("-1".equals(res)) {
                    datamodel.set(datamodel.size() - 1, String.valueOf(typeall.toArray()[0]));
                }
                if ("1".equals(res)) {
                    datamodel.set(datamodel.size() - 1, String.valueOf(typeall.toArray()[1]));
                }

                if ("-1".equals(orgres)) {
                    datamodel.set(datamodel.size() - 2, String.valueOf(typeall.toArray()[0]));
                }
                if ("1".equals(orgres)) {
                    datamodel.set(datamodel.size() - 2, String.valueOf(typeall.toArray()[1]));
                }

                datamodel.add(flag);

                result.add(datamodel);
            }

        }


        resultmap.put("result", result);

        Double zhengqueshu = 0.0;
        Double zongshu = 0.0;

        for (int i = 0; i < result.size(); i++) {
            ArrayList<String> resitem = result.get(i);
            if ("预测正确".equals(resitem.get(resitem.size() - 1))) {
                zhengqueshu++;
            }
            zongshu++;
        }

        resultmap.put("准确率", zhengqueshu / zongshu);
        Record record = new Record();
        record.setAcc(zhengqueshu / zongshu);
        record.setModel((String) data.get("model"));
        record.setOwner((String) data.get("owner"));
        record.setDataset((String) data.get("dataset"));
        record.setType("训练集");
        recorddao.insert(record);
        modeldao.update(record, cost);
        return resultmap;
    }

    public ArrayList<ArrayList<String>> readTable(String filePath) {
        ArrayList<String> d = null;
        ArrayList<ArrayList<String>> t = new ArrayList<ArrayList<String>>();
        File file = new File(filePath);
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(isr);
            String str = null;
            while ((str = bf.readLine()) != null) {
                d = new ArrayList<String>();
                String[] str1 = str.split(",");
                for (int i = 1; i < str1.length; i++) {
                    d.add(str1[i]);
                }
                t.add(d);

            }
            bf.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件不存在！");
        }

        // 处理数据格式化

        return t;
    }

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "trainresult")
    public List<Record> gettrainresult(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map
    ) throws IOException {

        List<Record> records = recorddao.select(map);
        return records;
    }

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "testresult")
    public List<Record> gettestresult(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map
    ) throws IOException {

        List<Record> records = recorddao.select(map);
        return records;
    }

    @ResponseBody
    @RequestMapping(value = "test")
    public Map test(HttpServletRequest request, HttpServletResponse response, @RequestBody Map data
    ) throws Exception {
        Map resultmap = new HashMap<>();//返回结果
        String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");
        String filepath = rootPath + (String) data.get("owner") + File.separator + (String) data.get("dataset");
        String modelpath = rootPath + (String) data.get("owner") + File.separator + "model" + File.separator + (String) data.get("model");
        ArrayList<ArrayList<String>> dataall = readTable(filepath);//找到数据集路线

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        Map temp = new HashMap();
        temp.put("owner", data.get("owner"));
        temp.put("model", data.get("model"));
        List<model> model = modeldao.selectOne(temp);
        if ("Ridge".equals(model.get(0).getType())) {

            //resultmap.put("type", "当前分析方法为SVM分类分析法");

            dataall.remove(0);

            SimpleSvm svm = new SimpleSvm(0.0001, 0.0, (String) data.get("model"), modelpath, model.get(0).getCost());

            Integer xlength = dataall.get(0).size();
            Integer ylength = dataall.size();

            double[] y = new double[ylength];
            double[][] X = new double[ylength][xlength];
            svm.loadAarray(X, y, dataall);

            svm.Train(X, y, 0);

            double[] test_y = new double[ylength];
            double[][] test_X = new double[ylength][xlength];
            svm.loadAarray(test_X, test_y, dataall);
            // svm.Test(test_X, test_y);

            for (int i = 0; i < test_X.length; i++) {
                ArrayList<String> datamodel = dataall.get(i);

                datamodel.add(String.valueOf(svm.predict(test_X[i])));

                String res = datamodel.get(datamodel.size() - 1);

                if ("-1".equals(res)) {
                    datamodel.set(datamodel.size() - 1, String.valueOf("clean"));
                }
                if ("1".equals(res)) {
                    datamodel.set(datamodel.size() - 1, "buggy");
                }


                result.add(datamodel);
            }

        }


        resultmap.put("result", result);
        String filepath1 = rootPath + (String) data.get("owner") + File.separator + "_predict"+(String) data.get("dataset");
        Text2csv(result,filepath1);
        Record record = new Record();
        record.setModel((String) data.get("model"));
        record.setOwner((String) data.get("owner"));
        record.setDataset((String) data.get("dataset"));
        record.setType("使用集");
        recorddao.insert(record);
        return resultmap;
    }

    public static void Text2csv(ArrayList<ArrayList<String>> list,String filepath) throws Exception {
        // 如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            //通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(filepath, true));
            //调用write的方法将字符串写到流中
            for (List<String> a: list) {
                for (String text:a){
                    writeText.append(text+",");
                }
                writeText.newLine();    //换行
            }
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }
}
