package com.zy.alg.nlp.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Sort {

    /**
     * List<Entry<String, Double>>按值进行降序排序
     *
     * @param Map
     * @return
     */
    public static List<Entry<String, Double>> sortMap(Map<String, Double> Map) {
        List<Map.Entry<String, Double>> SortMap = new ArrayList<Map.Entry<String, Double>>();
        if (null != Map && !Map.isEmpty()) {
            //构造器实现过程
            SortMap = new ArrayList<Map.Entry<String, Double>>(Map.entrySet());
            Collections.sort(SortMap, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
                    return m2.getValue().compareTo(m1.getValue());
                }
            });
            //
        }
        return SortMap;
    }

    /**
     * List<Entry<String, Double>>按键值"\t"分割后字符长度升序排序
     *
     * @param Map
     * @return
     */
    public static List<Entry<String, Double>> sortMapAsc(Map<String, Double> Map) {
        List<Map.Entry<String, Double>> SortMap = new ArrayList<Map.Entry<String, Double>>();
        if (null != Map && !Map.isEmpty()) {
            //构造器实现过程
            SortMap = new ArrayList<Map.Entry<String, Double>>(Map.entrySet());
            Collections.sort(SortMap, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
                    Double l1 = (double) (m1.getKey().split("\t").length);
                    Double l2 = (double) (m2.getKey().split("\t").length);
                    return l1.compareTo(l2);
                }
            });
            //
        }
        return SortMap;
    }

    /**
     * List<Entry<String, Float>>按值进行降序排序
     *
     * @param Map
     * @return
     */
    public static List<Entry<String, Float>> sortMapFloat(Map<String, Float> Map) {
        List<Map.Entry<String, Float>> SortMap = new ArrayList<Map.Entry<String, Float>>();
        if (null != Map && !Map.isEmpty()) {
            SortMap = new ArrayList<Map.Entry<String, Float>>(Map.entrySet());
            Collections.sort(SortMap, new Comparator<Map.Entry<String, Float>>() {
                @Override
                public int compare(Map.Entry<String, Float> m1, Map.Entry<String, Float> m2) {
                    return m2.getValue().compareTo(m1.getValue());
                }
            });
        }
        return SortMap;
    }

    /**
     * List<Entry<String, Integer>>按值进行降序排序
     *
     * @param Map
     * @return
     */
    public static List<Entry<String, Integer>> sortIntMap(Map<String, Integer> Map) {
        List<Map.Entry<String, Integer>> SortMap = new ArrayList<Map.Entry<String, Integer>>();
        if (null != Map && !Map.isEmpty()) {
            SortMap = new ArrayList<Map.Entry<String, Integer>>(Map.entrySet());
            Collections.sort(SortMap, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
                    return m2.getValue().compareTo(m1.getValue());
                }
            });
        }
        return SortMap;
    }

    /**
     * Map<Integer, String>按照键值进行升序排列——>可以采用TreeMap实现
     *
     * @param Map
     * @return
     */
    public static Map<Integer, String> sortIntMapChange(Map<Integer, String> Map) {
        List<Map.Entry<Integer, String>> SortMap = new ArrayList<Map.Entry<Integer, String>>();
        if (null != Map && !Map.isEmpty()) {
            SortMap = new ArrayList<Map.Entry<Integer, String>>(Map.entrySet());
            Collections.sort(SortMap, new Comparator<Map.Entry<Integer, String>>() {
                @Override
                public int compare(Map.Entry<Integer, String> m1, Map.Entry<Integer, String> m2) {
                    // 升序排列
                    return m1.getKey().compareTo(m2.getKey());
                }
            });
        }
        Map<Integer, String> SortMapChange = new LinkedHashMap<Integer, String>();
        for (Entry<Integer, String> q : SortMap) {
            SortMapChange.put(q.getKey(), q.getValue());
        }
        return SortMapChange;
    }

    public static void main(String argv[]) {
        Map<String, Double> map = null;
        List<Entry<String, Double>> result = sortMap(map);
        System.out.println(result);
    }
}
