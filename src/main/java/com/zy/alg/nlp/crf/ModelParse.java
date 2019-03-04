package com.zy.alg.nlp.crf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.zy.alg.nlp.utils.Sort;


public class ModelParse {
    //通用版本
    //权重矩阵维数
    int weightMatricNum = 0;
    //扩展模板个数
    int extendtempleNum = 0;
    int labelNum = 0;
    double[] weight = null;
    String[] label = null;
    /**
     * U00:%x[-2,0]
     */
    Template temp = null;
    /**
     * 20 U00:_B-1
     */
    ExpendTemplate extemp = null;

    public String useTestViterbi(List<String> words) {
        if (words.size() < 1) {
            return null;
        }
        boolean debug = false;
        Map<String, Map<String, Integer>> tempFeatureIds = extemp.tempFeatureIds;
        Map<String, List<String>> featureTemplatesplit = temp.featureTemplatesplit;

        Map<String, Double> typeScoreMap = new HashMap<>();
        Map<String, Double> typeScoreResultMap = new HashMap<>();

        // Bigram的id
        int idBigram = extemp.idBigram;
        Tables table = new Tables(words);
        if (debug) {
            System.out.println("---------------------");
        }
        if (debug) {
            System.out.println("words" + words);
        }

        // Viterbi 权重和路径
        // Viterbi
        double[] Viscoreag = new double[labelNum];
        // Viterbi
        Map<Integer, String> Vipath = new HashMap<Integer, String>();
        //转移权重
        double[][] bgramBEM = new double[labelNum][labelNum];
        for (int i = 0; i < labelNum; i++) {
            for (int j = 0; j < labelNum; j++) {
                bgramBEM[i][j] = weight[idBigram + (i * labelNum) + j];
            }
        }

        // transform
        Map<String, List<Double>> weightBEM = new HashMap<String, List<Double>>();
        for (int i = 0; i < label.length; i++) {
            String str = label[i];
            weightBEM.put(str, new ArrayList<Double>());
        }

        for (int i = 0, length = words.size(); i < length; i++) {

            Map<String, List<Double>> weightBEMtmp = new HashMap<String, List<Double>>();
            for (int j = 0; j < label.length; j++) {
                String str = label[j];
                weightBEMtmp.put(str, new ArrayList<Double>());
            }

            String word = "";
            if (words.get(i).contains("&&")) {
                word = words.get(i).split("&&")[0];
            } else {
                word = words.get(i);
            }

            if (debug) {
                System.out.println("word\t" + word);
            }
            for (Map.Entry<String, List<String>> entry : featureTemplatesplit.entrySet()) {
                // U00
                String tagsu = entry.getKey();
                List<String> templates = entry.getValue();
                StringBuffer sb = new StringBuffer();
                String template = "";
                for (int j = 0, length1 = templates.size(); j < length1; j++) {
                    template = templates.get(j);
                    String xy = template.replace("[", "").replace("]", "");
                    ;
                    int x = Integer.parseInt(xy.split(",")[0]);
                    int y = Integer.parseInt(xy.split(",")[1]);
                    if (!sb.toString().equals("")) {
                        sb.append("/");
                    }
                    sb.append(table.getTag(i + x, y));
                }
                // Map<String,Integer>featureIds =
                // tempFeatureIds.getOrDefault(tagsu, new
                // HashMap<String,Integer>());
                //
                Map<String, Integer> featureIds = new HashMap<String, Integer>();
                if (tempFeatureIds.containsKey(tagsu)) {
                    featureIds = tempFeatureIds.get(tagsu);
                }
                if (featureIds.size() > 0) {
                    String tagFeature = sb.toString();
                    if (featureIds.containsKey(tagFeature)) {
                        int id = featureIds.get(tagFeature);
                        if (debug) {
                            System.out.println("match Temp\t" + id + "\t" + tagsu + "\t" + template + "\t" + tagFeature
                                    + "\tC:" + weight[id] + "\tN:" + weight[id + 1]);
                        }
                        for (int j = 0; j < label.length; j++) {
                            String str = label[j];
                            List<Double> score = weightBEMtmp.get(str);
                            score.add(weight[id + j]);
                            weightBEMtmp.put(str, score);
                        }
                    }
                }
            }

            //词本身累积权重
            double[] sunBEM = new double[label.length];
            for (int j = 0; j < label.length; j++) {
                sunBEM[j] = 0;
                String str = label[j];
                List<Double> score = weightBEMtmp.get(str);
                for (int k = 0; k < score.size(); k++) {
                    sunBEM[j] += score.get(k);
                }
            }

            // --------------------- Viterbi ---------------------------
            if (i < 1) {
                for (int j = 0; j < labelNum; j++) {
                    Viscoreag[j] = sunBEM[j];
                    Vipath.put((j + 1), label[j]);
                }

            } else {
                //上一个词标记的路径分数
                double[] presBEM = new double[labelNum];
                for (int j = 0; j < labelNum; j++) {
                    presBEM[j] = Viscoreag[j];
                }

                // transform
                Map<Integer, String> Vipathsaved = new HashMap<Integer, String>();
                for (int j = 0; j < labelNum; j++) {
                    Vipathsaved.put((j + 1), Vipath.get((j + 1)));
                }
                // 到达状态j所有路径
                for (int j = 0; j < labelNum; j++) {
                    double[] skj = new double[labelNum];
                    for (int k = 0; k < labelNum; k++) {
                        skj[k] = presBEM[k] + bgramBEM[k][j] + sunBEM[j];
                    }
                    double maxScore = skj[0];
                    int maxIdex = 0;
                    for (int k = 1; k < labelNum; k++) {
                        if (skj[k] > maxScore) {
                            maxScore = skj[k];
                            maxIdex = k;
                        }
                    }
                    String pathPre = Vipathsaved.get((maxIdex + 1));
                    Vipath.put((j + 1), pathPre + "\t" + label[j]);
                    Viscoreag[j] = maxScore;
                }
            }
            for (Entry<Integer, String> v : Vipath.entrySet()) {
                typeScoreMap.put(v.getValue(), Viscoreag[v.getKey() - 1]);
            }
            // ------------------------------------------------------------
        }
        String tags = "";
        double maxScore = Viscoreag[0];
        int maxIndex = 0;
        for (int i = 1; i < labelNum; i++) {
            if (maxScore < Viscoreag[i]) {
                maxScore = Viscoreag[i];
                maxIndex = i;
            }
        }

        if (typeScoreMap.containsKey(Vipath.get(maxIndex + 1))) {
            typeScoreResultMap.put(Vipath.get(maxIndex + 1), typeScoreMap.get(Vipath.get(maxIndex + 1)));
            String[] seg = Vipath.get(maxIndex + 1).split("\t");
            for (int i = seg.length; i >= 0; i--) {
                StringBuilder queryMergeLine = new StringBuilder();
                typeScoreResultMap.put(seg[0], typeScoreMap.get(seg[0]));
                for (int j = 0; j < i; j++) {
                    if (j == i - 1) {
                        queryMergeLine.append(seg[j]);
                    } else {
                        queryMergeLine.append(seg[j] + "\t");
                    }

                }
                if (queryMergeLine.length() > 0 && typeScoreMap.containsKey(queryMergeLine.toString())) {
                    typeScoreResultMap.put(queryMergeLine.toString(), typeScoreMap.get(queryMergeLine.toString()));
                }
            }
        }

        StringBuffer result = new StringBuffer();
        List<Entry<String, Double>> ll = Sort.sortMapAsc(typeScoreResultMap);
        double ss = 0;
        for (Entry<String, Double> r : ll) {
            if (result.length() > 0) {
                String[] rSeg = r.getKey().split("\t");
                String firstLabel = rSeg[rSeg.length - 2];
                String secondLabel = rSeg[rSeg.length - 1];
                int firstNum = 0;
                int secondNum = 0;
                for (int i = 0; i < label.length; i++) {
                    if (firstLabel.equals(label[i])) {
                        firstNum = i;
                    }
                    if (secondLabel.equals(label[i])) {
                        secondNum = i;
                    }
                }
                double sc = r.getValue() - ss - bgramBEM[firstNum][secondNum];
                result.append(sc + "\t");
                ss = r.getValue();
            } else {
                result.append(r.getValue() + "\t");
                ss = r.getValue();
            }
        }

        if (result.length() > 0) {
            tags = Vipath.get(maxIndex + 1) + "##" + result.toString();
        } else {
            tags = Vipath.get(maxIndex + 1);
        }

        if (debug) {
            System.out.println("标签序列：" + tags);
        }
        return tags;

    }

    //app开发
    public String useTestViterbi_V1(List<String> words, Set<String> set) {
        if (words.size() < 1) {
            return null;
        }
        if (words.size() == 1) {
            return words.get(0).split("&&")[0];
        }
        boolean debug = false;
        Map<String, Map<String, Integer>> tempFeatureIds = extemp.tempFeatureIds;
        Map<String, List<String>> featureTemplatesplit = temp.featureTemplatesplit;
        //Bigram的id
        int idBigram = extemp.idBigram;
        Tables table = new Tables(words);
        if (debug) {
            System.out.println("---------------------");
        }
        if (debug) {
            System.out.println("words" + words);
        }
        //Viterbi 权重和路径
        //Viterbi
        double[] Viscoreag = new double[labelNum];
        //Viterbi
        Map<Integer, String> Vipath = new HashMap<Integer, String>();
        //Bigram 转移权值
        double[][] bgramBEM = new double[labelNum][labelNum];
        for (int i = 0; i < labelNum; i++) {
            for (int j = 0; j < labelNum; j++) {
                bgramBEM[i][j] = weight[idBigram + (i * labelNum) + j];
            }
        }

        //transform
        Map<String, List<Double>> weightBEM = new HashMap<String, List<Double>>();
        for (int i = 0; i < label.length; i++) {
            String str = label[i];
            weightBEM.put(str, new ArrayList<Double>());
        }

        for (int i = 0, length = words.size(); i < length; i++) {

            Map<String, List<Double>> weightBEMtmp = new HashMap<String, List<Double>>();
            for (int j = 0; j < label.length; j++) {
                String str = label[j];
                weightBEMtmp.put(str, new ArrayList<Double>());
            }

            String word = "";
            if (words.get(i).contains("&&")) {
                word = words.get(i).split("&&")[0];
            } else {
                word = words.get(i);
            }

            if (debug) {
                System.out.println("word\t" + word);
            }
            for (Map.Entry<String, List<String>> entry : featureTemplatesplit.entrySet()) {
                //U00
                String tagsu = entry.getKey();
                List<String> templates = entry.getValue();
                StringBuffer sb = new StringBuffer();
                String template = "";
                for (int j = 0, length1 = templates.size(); j < length1; j++) {
                    template = templates.get(j);
                    String xy = template.replace("[", "").replace("]", "");
                    ;
                    int x = Integer.parseInt(xy.split(",")[0]);
                    int y = Integer.parseInt(xy.split(",")[1]);
                    if (!sb.toString().equals("")) {
                        sb.append("/");
                    }
                    sb.append(table.getTag(i + x, y));
                }
                //Map<String,Integer>featureIds = tempFeatureIds.getOrDefault(tagsu, new HashMap<String,Integer>());
                //
                Map<String, Integer> featureIds = new HashMap<String, Integer>();
                if (tempFeatureIds.containsKey(tagsu)) {
                    featureIds = tempFeatureIds.get(tagsu);
                }
                //
                if (featureIds.size() > 0) {
                    String tagFeature = sb.toString();
                    if (featureIds.containsKey(tagFeature)) {
                        int id = featureIds.get(tagFeature);
                        if (debug) {
                            System.out.println("match Temp\t" + id + "\t" + tagsu + "\t" + template + "\t" + tagFeature + "\tC:" + weight[id] + "\tN:" + weight[id + 1]);
                        }
                        for (int j = 0; j < label.length; j++) {
                            String str = label[j];
                            List<Double> score = weightBEMtmp.get(str);
                            score.add(weight[id + j]);
                            weightBEMtmp.put(str, score);
                        }
                    }
                }
            }

            double[] sunBEM = new double[label.length];
            for (int j = 0; j < label.length; j++) {
                sunBEM[j] = 0;
                String str = label[j];
                List<Double> score = weightBEMtmp.get(str);
                for (int k = 0; k < score.size(); k++) {
                    sunBEM[j] += score.get(k);
                }
            }


            //---------------------  Viterbi  ---------------------------
            if (i < 1) {
                for (int j = 0; j < labelNum; j++) {
                    Viscoreag[j] = sunBEM[j];
                    Vipath.put((j + 1), label[j]);
                }
            } else {
                double[] presBEM = new double[labelNum];
                for (int j = 0; j < labelNum; j++) {
                    presBEM[j] = Viscoreag[j];
                }

                //transform
                Map<Integer, String> Vipathsaved = new HashMap<Integer, String>();
                for (int j = 0; j < labelNum; j++) {
                    Vipathsaved.put((j + 1), Vipath.get((j + 1)));
                }
                //到达状态j所有路径
                for (int j = 0; j < labelNum; j++) {
                    double[] skj = new double[labelNum];
                    for (int k = 0; k < labelNum; k++) {
                        skj[k] = presBEM[k] + bgramBEM[k][j] + sunBEM[j];
                    }
                    double maxScore = skj[0];
                    int maxIdex = 0;
                    for (int k = 1; k < labelNum; k++) {
                        if (skj[k] > maxScore) {
                            maxScore = skj[k];
                            maxIdex = k;
                        }
                    }
                    String pathPre = Vipathsaved.get((maxIdex + 1));
                    Vipath.put((j + 1), pathPre + "\t" + label[j]);
                    Viscoreag[j] = maxScore;
                }
            }
            //------------------------------------------------------------

        }
        String tags = "";
        double maxScore = Viscoreag[0];
        int maxIndex = 0;
        for (int i = 1; i < labelNum; i++) {
            if (maxScore < Viscoreag[i]) {
                maxScore = Viscoreag[i];
                maxIndex = i;
            }
        }
        tags = Vipath.get(maxIndex + 1);
        if (debug) {
            System.out.println("标签序列：" + tags);
        }
        return tags;

    }

    public void parse(String path) {
        //U00:%x[-2,0]
        temp = new Template();
        //20 U00:_B-1
        extemp = new ExpendTemplate();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(path)), "utf-8"));
            //头文件
            String version = br.readLine();
            String costfactor = br.readLine();
            weightMatricNum = Integer.parseInt(br.readLine().split(": ")[1]);

            weight = new double[weightMatricNum];
            String xsize = br.readLine();
            //标签
            br.readLine();
            String Line = "";
            String str = "";
            while ((str = br.readLine()).length() > 0) {
                Line += str;
                Line += "\t";
            }
            String[] Lines = Line.trim().split("\t");
            labelNum = Lines.length;
            extendtempleNum = (weightMatricNum - labelNum * labelNum) / labelNum + 1;
            label = new String[labelNum];
            for (int i = 0; i < labelNum; i++) {
                label[i] = Lines[i];
            }
            //模板
            String line = "";
            int weightindex = 0;
            int hang = 0;
            while ((line = br.readLine()) != null) {
                hang++;
                if (line.split(" ").length > 1 && line.split(" ")[1].toLowerCase().equals("b") && line.length() > 1) {
                    extemp.addBigramTag(line);
                    continue;
                }
                if (line.toLowerCase().contains("u")) {
                    //模板
                    if (line.toLowerCase().contains("%x[")) {
                        temp.addTemplateSplit(line);
                    } else {
                        //扩展模板
                        extemp.addBigramTag1(line);
                    }
                } else if (hang > extendtempleNum && !line.equals("")) {
                    weight[weightindex] = Double.parseDouble(line);
                    weightindex++;
                }

                line = "";
            }
            br.close();
            System.out.println("parse ok");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
