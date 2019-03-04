package com.zy.alg.nlp.crf_labeling;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 检查标注的文件是否属性缺失-以备后续训练模型
 */
public class AttributionConfirm {

    public static void main(String[] args) throws IOException {
        String originalFilePath = "F:\\序列标注\\搜索属性识别\\已确认部分\\";
        BufferedReader tr = new BufferedReader(new InputStreamReader(
                new FileInputStream(originalFilePath + "crftrainData.final"), "utf-8"));
        BufferedWriter trr = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(originalFilePath + "crftrainData.final.question"), "utf-8"));

        Set<String> label = new HashSet<String>();
        int num = 0;
        String line;
        while ((line = tr.readLine()) != null) {
            num++;
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                label.add(seg[2]);
            } else if (seg.length > 1) {
                trr.write(num + "#" + "\t" + line + "\n");
            }
        }
        tr.close();

        for (String q : label) {
            trr.write(q + "\n");
        }
        trr.close();
    }
}
