package org.spring.springboot.test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SimpleSvm 
{
	private String modelname;
	private String filePath;
	private int exampleNum;
	private int exampleDim;
	private double[] w;
	private final double lambda;
	private double lr = 0.001;//0.00001
	private double cost;
	private double min_cost;
	private double[] grad;
	private double[] yp;
	//可以初始化 1：w 2:lr 3:threshold(退出值) 4:lambda(正则)
	//保存的 w
	public SimpleSvm(double paramLambda,Double step,String name,String filepath,Double cost)
	{

		lambda = paramLambda;	
		lr=step;
		modelname=name;
		filePath=filepath;
		min_cost=cost;
	}
	
	private void CostAndGrad(double[][] X,double[] y)
	{
		cost =0;
		for(int m=0;m<exampleNum;m++)
		{
			yp[m]=0;
			for(int d=0;d<exampleDim;d++)
			{
				yp[m]+=X[m][d]*w[d];
			}
			
			if(y[m]*yp[m]-1<0)
			{
				cost += (1-y[m]*yp[m]);
			}
			
		}
		
		for(int d=0;d<exampleDim;d++)
		{
			cost += 0.5*lambda*w[d]*w[d];
		}
		

		for(int d=0;d<exampleDim;d++)
		{
			grad[d] = Math.abs(lambda*w[d]);	
			for(int m=0;m<exampleNum;m++)
			{
				if(y[m]*yp[m]-1<0)
				{
					grad[d]-= y[m]*X[m][d];
				}
			}
		}				
	}
	
	private void update()
	{
		for(int d=0;d<exampleDim;d++)
		{
			w[d] -= lr*grad[d];
		}
	}
	
	public Double Train(double[][] X,double[] y,int maxIters)
	{
		exampleNum = X.length;
		exampleDim = X[0].length;
		w = new double[exampleDim];
		File file=new File(filePath);
		readFromFile(file);
		//初始化w
		grad = new double[exampleDim];
		yp = new double[exampleNum];
		int save=0;
		for(int iter=0;iter<maxIters;iter++)
		{
			
			CostAndGrad(X,y);
//			System.out.println("cost:"+cost);

			if(cost< min_cost)
			{
				min_cost=cost;
				save=1;
				continue;
			}
			update();
		}
		if(save==1)
		{
			writeToFile(w,file);
		}
		//writeToFile(w,file);
		return min_cost;
	}
	private void writeToFile(double[] w,File file){
		FileWriter out = null;
		try {
			out = new FileWriter(file);
			//二维数组按行存入到文件中
			for (int i = 0; i < w.length; i++) {
				//将每个元素转换为字符串
				String content = String.valueOf(w[i]) + "";
				out.write(content + " ");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readFromFile(File file){
		if(file.length()==0)
		{
			return ;
		}
		BufferedReader bufferedReader = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
				line = bufferedReader.readLine();
				if(null != line){
					//将按行读取的字符串按空格分割，得到一个string数组
					String[] strings = line.split(" ");

					for(int k = 0;k<strings.length-1;k++){
						w[k] = Double.parseDouble(strings[k]);
					}
				}
			}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int predict(double[] x)
	{
		File file=new File(filePath);
		readFromFile(file);
		double pre=0;
		for(int j=0;j<x.length;j++)
		{
			pre+=x[j]*w[j];
		}
		if(pre >=0)//这个阈值一般位于-1到1
			return 1;
		else return -1;
	}
	
	public static Set<String> huoqutype(ArrayList<ArrayList<String>> dataall) {

		Set<String> typeall = new HashSet<String>();
		
		for (int i = 1; i < dataall.size(); i++) {
			typeall.add(dataall.get(i).get(dataall.get(i).size() - 1));
		}
		
		
		return typeall;
	}

	public void loadAarray(double[][] X, double[] y, ArrayList<ArrayList<String>> dataall) {
		
		
		for (int i = 0; i < dataall.size(); i++) {
			
			ArrayList<String> chuliitem = dataall.get(i);
			
			y[i] = Double.parseDouble(chuliitem.get(chuliitem.size() - 1));
			//System.out.println(y[index]);
			
			for (int j = 0; j < chuliitem.size(); j++) {
				double v = Double.parseDouble(chuliitem.get(j));
				X[i][j] = v;
			}
			
			//X[i][0] =1;
		}
		
	}




}
