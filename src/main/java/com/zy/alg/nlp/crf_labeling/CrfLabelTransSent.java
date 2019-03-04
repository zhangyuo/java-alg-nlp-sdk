package com.zy.alg.nlp.crf_labeling;

import java.io.*;

/**
 * 标注文本转化为原始文本
 */
public class CrfLabelTransSent {
	
	public static void main(String argv[]) throws IOException {
		String corpusPath = "F:\\序列标注\\搜索属性识别\\已确认部分\\";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(corpusPath + "crftrainData00"), "utf-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(corpusPath+"crftrainData00.new"),"utf-8"),true);
		String line;
		String sent = "";
		int num = 0;
		while((line = br.readLine()) != null){
			System.out.println(++num);
			String[] seg = line.split("\t");
			if(seg.length == 3){
				sent += seg[0];
			} else {
				pw.println(sent);
				sent = "";
			}
		}
		br.close();
		pw.close();
	}

}
