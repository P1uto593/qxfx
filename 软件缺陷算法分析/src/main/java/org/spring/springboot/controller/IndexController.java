package org.spring.springboot.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.springboot.dao.AdminMapper;
import org.spring.springboot.dao.DatasetMapper;
import org.spring.springboot.dao.ModelMapper;
import org.spring.springboot.domain.Admin;
import org.spring.springboot.domain.Dataset;
import org.spring.springboot.domain.model;
import org.spring.springboot.test.KNN;
import org.spring.springboot.test.KNN.KnnModel;
import org.spring.springboot.test.SimpleSvm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller

@RequestMapping(value = "/")

public class IndexController {


	@Autowired
	private AdminMapper admindao;

	@ResponseBody
	// 定义loginactjson，处理登录
	@RequestMapping(value = "loginactjson")

	public Map loginactjson(HttpServletRequest request, HttpServletResponse response, @RequestBody Map canshu,
			HttpSession session) {
		Map result = new HashMap();
		// 生成用户对象
		Admin admin = new Admin();
		// 从传入参数中获取账号信息
		admin.setUsername((String) canshu.get("username"));
		// 从传入参数中获取密码信息
		admin.setPassword((String) canshu.get("password"));
		List<Admin> admins = admindao.selectByUser(admin);

		// 如果结果为空

		if (admins==null) {

			// 将账户或密码错误保存到message中

			result.put("message", "账户或密码错误");
			// 返回resultmap对象

			return result;
		} else {

			// 登录成功，将登录成功保存到message中

			result.put("message", "登录成功");
			// 登录成功，将名字保存到result中

			result.put("mingzi", admins.get(0).getUsername());
			// 登录成功，将登录用户id保存到result中

			result.put("id", admins.get(0).getId());
			// 登录成功，将登录用户保存到result中

			result.put("userinfo", admins.get(0));

			// 返回resultmap对象

			return result;
		}
	}

	@ResponseBody
	// 定义registactjson，处理注册

	@RequestMapping(value = "registactjson")

	public Map registactjson(HttpServletRequest request, HttpServletResponse response, @RequestBody Map canshu,
			HttpSession session) throws Exception {

		// 输出注册日志，表示当前正在执行loginAndRegistController.registactjson

		Map result = new HashMap();
		// 获取当前登录的用户身份
		// 从传入参数中获取确认密码信息
		String repassword = (String) canshu.get("repassword");
		// 生成用户对象
		Admin admin = new Admin();
		// 从传入参数中获取账号信息
		admin.setUsername((String) canshu.get("username"));
		// 从传入参数中获取密码信息
		admin.setPassword((String) canshu.get("password")); // 判断两次密码是否一致
		if (!repassword.equals(admin.getPassword())) {

			// 将账号已存在信息保存到result的message中进行保存
			result.put("message", "两次密码不一致");
			// 将result用json格式数据返回页面
			return result;
		} // 判断管理员和identity是否一致


		// 根据上方查询条件查询管理员表中username为传入username的数据

		List<Admin> admins = admindao.selectByUsername(admin);

		// 如果查询结果不为空

		if (!admins.isEmpty()) {

			// 将账号已存在信息保存到result的message中进行保存
			result.put("message", "该账号已存在");
			// 将result用json格式数据返回页面
			return result;
		} else {

			// 使用admindao的 insert方法将页面传入的管理员数据添加到数据库中

			admindao.insert(admin);

			// 将注册成功，请登录信息保存到result的message中进行保存
			result.put("message", "注册成功，请登录");
			// 将result用json格式数据返回页面
			return result;
		}
	}

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		return "index";
	}

	@ResponseBody
	@RequestMapping(value = "fenxiactjson")
	public Map fenxiactjson(@RequestBody Map map) throws IOException {

		Map resultmap = new HashMap<>();

		String filepath = (String) map.get("csv");
		String type = (String) map.get("type");

		ArrayList<ArrayList<String>> dataall = readTable(filepath);

		ArrayList<ArrayList<String>> result = new ArrayList<>();


		if ("SVM算法".equals(type)) {

			resultmap.put("type", "当前分析方法为SVM分类分析法");

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

			SimpleSvm svm = new SimpleSvm(0.0001,0.0,"a","a",0.0);

			Integer xlength = dataall.get(0).size();
			Integer ylength = dataall.size();
			Integer train_ylength = trainall.size();

			double[] y = new double[train_ylength];
			double[][] X = new double[train_ylength][xlength];
			svm.loadAarray(X, y, trainall);

			svm.Train(X, y, 7000);

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

		if ("KNN算法".equals(type)) {
			resultmap.put("type", "当前分析方法为KNN分类分析法");
			List<KnnModel> knnModelList = new ArrayList<>();

			for (int i = 1; i < 50; i++) {

				ArrayList<String> dataitem = dataall.get(i);

				ArrayList<Double> list1 = new ArrayList<>();

				for (int j = 0; j < dataitem.size() - 2; j++) {
					list1.add(Double.parseDouble(dataitem.get(j)));
				}
				knnModelList.add(new KnnModel(list1, dataitem.get(dataitem.size() - 1)));
			}

			for (int i = 1; i < dataall.size(); i++) {

				ArrayList<String> datamodel = dataall.get(i);

				ArrayList<Double> listm = new ArrayList<>();

				for (int j = 0; j < datamodel.size() - 2; j++) {
					listm.add(Double.parseDouble(datamodel.get(j)));
				}

				KnnModel model = new KnnModel(listm, null);

				String res = KNN.calculateKnn(knnModelList, model, 3);

				datamodel.add(res);

				String flag = "";

				if (res.equals(datamodel.get(datamodel.size() - 2))) {
					flag = "预测正确";
				} else {
					flag = "预测错误";
				}
				datamodel.add(flag);

				result.add(datamodel);

			}
		}

		resultmap.put("result", result);

		Integer zhengqueshu = 0;
		Integer zongshu = 0;

		HashMap<String, Integer> jieguo = new HashMap<>();

		for (int i = 0; i < result.size(); i++) {
			ArrayList<String> resitem = result.get(i);

			String nowtype = resitem.get(resitem.size() - 2);

			if ("预测正确".equals(resitem.get(resitem.size() - 1))) {
				zhengqueshu++;
			}

			zongshu++;

			int mapflag = 0;
			for (Map.Entry<String, Integer> entry : jieguo.entrySet()) {
				String mapKey = entry.getKey();

				if (mapKey.equals(nowtype)) {
					mapflag = 1;
				}

			}

			if (mapflag == 0) {
				jieguo.put(nowtype, 1);
			} else {
				jieguo.put(nowtype, jieguo.get(nowtype) + 1);
			}

		}

		resultmap.put("jieguo", jieguo);
		resultmap.put("zhengqueshu", zhengqueshu);
		resultmap.put("zongshu", zongshu);

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

	// 返回json字符串
	@ResponseBody
	// 添加文件ajax处理
	@RequestMapping(value = "addfilejson")
	public String addfilejson(HttpServletRequest request, HttpServletResponse response, MultipartFile file,
			HttpSession session) throws IOException {
		String filepath = uploadUtile(file, request);
		return filepath;
	}


	// 上传文件图片等
	public String uploadUtile(MultipartFile file, HttpServletRequest request) throws IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");

		String res = sdf.format(new Date());

		// uploads文件夹位置

		String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");

		// 原始名称

		String originalFileName = file.getOriginalFilename();

		// 新文件名

		String newFileName = "sliver" + res + originalFileName.substring(originalFileName.lastIndexOf("."));

		// 创建年月文件夹

		Calendar date = Calendar.getInstance();

		File dateDirs = new File(date.get(Calendar.YEAR) + File.separator + (date.get(Calendar.MONTH) + 1));

		// 新文件

		File newFile = new File(rootPath + File.separator + dateDirs + File.separator + newFileName);

		// 判断目标文件所在目录是否存在

		if (!newFile.getParentFile().exists()) {

			// 如果目标文件所在的目录不存在，则创建父目录

			newFile.getParentFile().mkdirs();

		}

		System.out.println(newFile);

		// 将内存中的数据写入磁盘

		file.transferTo(newFile);

		// 完整的url

		String fileUrl = date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1) + "/" + newFileName;

		return newFile.getAbsolutePath();

	}
}
