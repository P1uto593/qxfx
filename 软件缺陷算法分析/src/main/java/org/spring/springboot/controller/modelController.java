package org.spring.springboot.controller;

import org.spring.springboot.dao.ModelMapper;
import org.spring.springboot.domain.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller

@RequestMapping(value = "/")
public class modelController {
    @Autowired
    private ModelMapper modeldao;

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "newModel")
    public String newModel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map canshu
    ) throws IOException {

        model new_model=new model();
        // 从传入参数中获取账号信息
        new_model.setName((String) canshu.get("name"));
        new_model.setType((String) canshu.get("type"));
        new_model.setOwner((String) canshu.get("owner"));
        new_model.setAcc((Double) 0.0);
        new_model.setCost(99999.99);
        modeldao.insert(new_model);

        createModel(request,(String) canshu.get("name"),(String) canshu.get("owner"));
        return "1";
    }

    private void createModel(HttpServletRequest request,String name, String owner) {
        String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");
        //uploads/123/model/test
        String Path=rootPath+ File.separator+owner+File.separator+"model"+File.separator+name;
        File file = new File(Path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "getmodel")
    public List<model> gerModel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map nowuser
    ) throws IOException {

        List<model> models=modeldao.select(nowuser);
        return models;
    }
    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "deletemodel")
    public void deleteModel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map
    ) throws IOException {

        modeldao.delete(map);
        return ;
    }

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "getecharts")
    public List<Double> gerEcharts(HttpServletRequest request, HttpServletResponse response, @RequestBody Map nowuser
    ) throws IOException {

        List<Double> data=modeldao.selectEcharts(nowuser);
        return data;
    }
    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "lookmodel")
    public Map lookmodel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map data
    ) throws IOException {
        String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");
        File file=new File(rootPath+(String)data.get("owner")+File.separator+"model"+File.separator+(String) data.get("name"));
        if(file.length()==0)
        {
            return null;
        }
        BufferedReader bufferedReader = null;
        String[] strings=null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            line = bufferedReader.readLine();
            if(null != line){
                //将按行读取的字符串按空格分割，得到一个string数组
                strings = line.split(" ");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Map result=new HashMap();
        Map temp=new HashMap();
        for (int i = 0; i <strings.length ; i++) {
            temp.put(String.valueOf(i+1),String.format("%.4f",Double.parseDouble(strings[i])));
        }
        result.put("arr",temp);
        return result;
    }
}
