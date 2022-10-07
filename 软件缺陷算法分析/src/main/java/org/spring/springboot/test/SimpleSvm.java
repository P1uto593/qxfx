package org.spring.springboot.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SimpleSvm 
{
	private int exampleNum;
	private int exampleDim;
	private double[] w;
	private final double lambda;
	private final double lr = 0.001;//0.00001
	private final double threshold = 0.001;
	private double cost;
	private double[] grad;
	private double[] yp;
	public SimpleSvm(double paramLambda)
	{

		lambda = paramLambda;	
		
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
	
	public void Train(double[][] X,double[] y,int maxIters)
	{
		exampleNum = X.length;
		if(exampleNum <=0) 
		{
			System.out.println("num of example <=0!");
			return;
		}
		exampleDim = X[0].length;
		w = new double[exampleDim];
		grad = new double[exampleDim];
		yp = new double[exampleNum];
		
		for(int iter=0;iter<maxIters;iter++)
		{
			
			CostAndGrad(X,y);
//			System.out.println("cost:"+cost);
			if(cost< threshold)
			{
				break;
			}
			update();
			
		}
	}
	public int predict(double[] x)
	{
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
	
	public void Test(double[][] testX,double[] testY)
	{
		int error=0;
		for(int i=0;i<testX.length;i++)
		{	
			
			if(predict(testX[i]) != testY[i])
			{
				
				error++;
			}
		}
		System.out.println("total:"+testX.length);
		System.out.println("error:"+error);
		System.out.println("error rate:"+((double)error/testX.length));
		System.out.println("acc rate:"+((double)(testX.length-error)/testX.length));
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
	
	
	public void loadData(double[][]X,double[] y,String trainFile) throws IOException
	{
		
		File file = new File(trainFile);
		RandomAccessFile raf = new RandomAccessFile(file,"r");
		StringTokenizer tokenizer,tokenizer2; 

		int index=0;
		while(true)
		{
			String line = raf.readLine();
			
			if(line == null) break;
			tokenizer = new StringTokenizer(line," ");
			y[index] = Double.parseDouble(tokenizer.nextToken());
			//System.out.println(y[index]);
			while(tokenizer.hasMoreTokens())
			{
				tokenizer2 = new StringTokenizer(tokenizer.nextToken(),":");
				int k = Integer.parseInt(tokenizer2.nextToken());
				double v = Double.parseDouble(tokenizer2.nextToken());
				X[index][k] = v;
				//System.out.println(k);
				//System.out.println(v);				
			}	
			X[index][0] =1;
			index++;		
		}
	}
	
	public static void main(String[] args) throws IOException 
	{	
		
		SimpleSvm svm = new SimpleSvm(0.0001);
		double[] y = new double[400];
		double[][] X = new double[400][11];
		String trainFile = "train_bc";
		svm.loadData(X,y,trainFile);
		
		svm.Train(X,y,7000);
		
		System.out.println(y[1]);
		
		double[] test_y = new double[283];
		double[][] test_X = new double[283][11];
		String testFile = "test_bc";
		svm.loadData(test_X,test_y,testFile);
		svm.Test(test_X, test_y);
		
	}



}
