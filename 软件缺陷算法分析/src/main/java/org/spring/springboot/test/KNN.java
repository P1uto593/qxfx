package org.spring.springboot.test;

import java.util.*;

/**
 * @author swh
 * 近邻分类器
 */
public class KNN {

    // 数据模型
    public static class KnnModel implements Comparable<KnnModel>{
        public List<Double> paramlist;
        public double distance;
        String type;
        public double getDistance() {
            return distance;
        }

        public KnnModel(List<Double> paramlist, String type) {
            this.paramlist = paramlist;
            this.type = type;
        }

        @Override
        public int compareTo(KnnModel model) {
            return Double.compare(this.distance, model.distance);
        }
    }

    /** 计算新数据与训练数据的距离 **/
    private static List<KnnModel> calculate(List<KnnModel> modelList, KnnModel model, int k) {
        for (KnnModel m : modelList) {
        	double gap = 0.0;
        	for (int i = 0; i < model.paramlist.size(); i++) {
        		double distanceitem = Math.abs(model.paramlist.get(i) - m.paramlist.get(i));
        		
        		gap += distanceitem;
        		
			}
            
            // 训练数据保存与目标数据的距离，方便下一步排序
            m.distance = gap;
        }
        // 根据distance大小进行排序(从小到大)
        Collections.sort(modelList, Comparator.comparingDouble(KnnModel::getDistance));
        // 返回差距最小的k个值
        List<KnnModel> resultList = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            resultList.add(modelList.get(i));
        }
        return resultList;
    }

    /** 统计出最多的类型 **/
    private static String findTypeByScope(List<KnnModel> modelList) {
        Map<String, Integer> typeMap = new HashMap<>(modelList.size());
        // 统计类型
        for (KnnModel model : modelList) {
            int sum = typeMap.get(model.type) == null ? 1 : typeMap.get(model.type) + 1;
            typeMap.put(model.type, sum);
        }
        // 返回出现次数最多的类型
        List<Map.Entry<String,Integer>> list = new ArrayList(typeMap.entrySet());
        Collections.sort(list, Comparator.comparingInt(Map.Entry::getValue));
        return list.get(list.size()-1).getKey();
    }

    /** Knn
     * @param modelList 训练数据集
     * @param model 待分类数据
     * @param k 范围变量
     * */
    public static String calculateKnn(List<KnnModel> modelList, KnnModel model, int k) {
        // (1) 计算训练数据与待分类数据的各自相对距离，并返回差距最小的K个训练结果
        List<KnnModel> minKnnList = calculate(modelList, model, k);
        // (2) 找出差距最小的K个结果中，最多的类型
        return findTypeByScope(minKnnList);
    }

    public static void main(String[] args) {
        List<KnnModel> knnModelList = new ArrayList<>();
        ArrayList<Double> list1 = new ArrayList<>();
        list1.add(180.0);
        list1.add(73.0);
        list1.add(96.0);
        list1.add(10000.0);
        knnModelList.add(new KnnModel(list1, "帅哥"));
        ArrayList<Double> list2 = new ArrayList<>();
        list2.add(183.0);
        list2.add(80.0);
        list2.add(95.0);
        list2.add(10000.0);
        knnModelList.add(new KnnModel(list2, "帅哥"));
        ArrayList<Double> list3 = new ArrayList<>();
        list3.add(173.0);
        list3.add(75.0);
        list3.add(95.0);
        list3.add(5000.0);
        knnModelList.add(new KnnModel(list3, "普通"));
        ArrayList<Double> list4 = new ArrayList<>();
        list4.add(170.0);
        list4.add(72.0);
        list4.add(80.0);
        list4.add(5000.0);
        knnModelList.add(new KnnModel(list4, "普通"));
        ArrayList<Double> list5 = new ArrayList<>();
        list5.add(171.0);
        list5.add(71.0);
        list5.add(89.0);
        list5.add(5000.0);
        knnModelList.add(new KnnModel(list5, "普通"));
        ArrayList<Double> list6 = new ArrayList<>();
        list6.add(155.0);
        list6.add(70.0);
        list6.add(60.0);
        list6.add(1000.0);
        knnModelList.add(new KnnModel(list6, "屌丝"));
        ArrayList<Double> list7 = new ArrayList<>();
        list7.add(159.0);
        list7.add(80.0);
        list7.add(68.0);
        list7.add(1000.0);
        knnModelList.add(new KnnModel(list7, "屌丝"));
        ArrayList<Double> list8 = new ArrayList<>();
        list8.add(160.0);
        list8.add(75.0);
        list8.add(70.0);
        list8.add(1000.0);
        knnModelList.add(new KnnModel(list8, "屌丝"));
        // 预测数据
        ArrayList<Double> listm = new ArrayList<>();
        listm.add(176.5);
        listm.add(70.0);
        listm.add(92.0);
        listm.add(5000.0);
        KnnModel model = new KnnModel(listm, null);
        // 输出预测类型
        System.out.println(calculateKnn(knnModelList, model, 3));
    }

}
