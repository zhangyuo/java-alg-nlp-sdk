package com.zy.alg.nlp.crf_labeling;

import com.zy.alg.nlp.utils.MyEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * crf模型自动转化标注问题-seg分词变化情况
 * 即：分词项目发生变化，前后两次标注的分词结果不一致情况
 *
 * @author zhangyu
 */
public class CrfLabelSegTransAuto {

    public static void main(String[] args) throws IOException {
        String corpusPath = "E:/项目/序列标注/搜索属性识别/已确认部分/";

        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData00"), "utf-8"));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData01"), "utf-8"));
        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData01.new"), "utf-8"), true);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData00.new"), "utf-8"), true);

        String line;
        List<MyEntry> oriSent = new ArrayList<MyEntry>();
        int num = 0;
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                MyEntry<String, String> me = new MyEntry<String, String>(seg[0], seg[2]);
                oriSent.add(me);
            } else {
                MyEntry<String, String> me = new MyEntry<String, String>("#seg#", "null");
                oriSent.add(me);
            }
        }
        br.close();

        String line1 = null;
        List<String> list = new ArrayList<String>();
        int flagall = 0;
        int num1 = 0;
        while ((line1 = br1.readLine()) != null) {
            System.out.println(++num1);
            String[] seg = line1.split("\t");
            if (seg.length == 2) {
                list.add(line1);
            } else {
                // old label segmenet with a sentence
                List<MyEntry> oriList = new ArrayList<MyEntry>();
                for (MyEntry<String, String> q : oriSent) {
                    if (q.getKey().equals("#seg#")) {
                        break;
                    }

                    oriList.add(q);
                }
                //compare
                if (list.size() == oriList.size()) {
                    int tmpNum = 0;
                    int flagNum = 0;
                    for (MyEntry<String, String> v : oriList) {
                        String key = list.get(tmpNum).split("\t")[0];
                        if (v.getKey().equals(key)) {
                            flagNum++;
                        }
                        tmpNum++;
                    }
//					int tmpNum1 = 0;
                    if (flagNum == list.size()) {
                        /**相同分词结构**/
//						flagall++;
//						for(String l : list){
//							pw.println(l+oriList.get(tmpNum1).getValue());
//							tmpNum1++;
//						}
//						pw.println();
                    } else {
                        /**相同行不同分词**/
                        flagall++;
                        for (MyEntry<String, String> m : oriList) {
                            pw.println(m.getKey() + "\t" + m.getValue());
                        }
                        pw.println();
                        int tmpNum2 = 0;
                        for (String n : list) {
                            String word = n.split("\t")[0];
                            String key = (String) oriList.get(tmpNum2).getKey();
                            String value = (String) oriList.get(tmpNum2).getValue();
                            if (word.equals(key)) {
                                pw1.println(n + value);
                                tmpNum2++;
                            } else if (word.contains(key)) {
                                Set<String> labelSet = new LinkedHashSet<String>();
                                labelSet.add(value);
                                Boolean flag = true;
                                int len = key.length();
                                while (flag) {
                                    tmpNum2++;
                                    if (len >= word.length()) {
                                        break;
                                    } else {
                                        key = (String) oriList.get(tmpNum2).getKey();
                                        value = (String) oriList.get(tmpNum2).getValue();
                                        if (!word.contains(key)) {
                                            flag = false;
                                        } else {
                                            labelSet.add(value);
                                            len += key.length();
                                        }
                                    }
                                }
                                String label2 = "";
                                if (labelSet.size() == 1) {
                                    for (String q : labelSet) {
                                        label2 = q;
                                    }
                                } else if (labelSet.contains("ID") && !labelSet.contains("TH")) {
                                    label2 = "ID";
                                } else if (labelSet.contains("TH")) {
                                    label2 = "TH";
                                } else if (labelSet.size() == 2) {
                                    if (labelSet.contains("OT")) {
                                        labelSet.remove("OT");
                                        for (String q : labelSet) {
                                            label2 = q;
                                        }
                                    } else {
                                        label2 = "UNKNOWN";
                                    }
                                } else {
                                    label2 = "UNKNOWN";
                                }
                                pw1.println(n + label2);
                            } else if (key.contains(word)) {
                                pw1.println(n + value);
                            } else {
                                tmpNum2++;
                                key = (String) oriList.get(tmpNum2).getKey();
                                value = (String) oriList.get(tmpNum2).getValue();
                                if (word.contains(key)) {
                                    pw1.println(n + value);
                                    tmpNum2++;
                                } else {
                                    pw1.println(n + "UNKNOWN");
                                }
                            }
                        }
                        pw1.println();
                    }
                } else {
                    /**不相同行不同分词**/
//					flagall++;
//					for(MyEntry<String,String> m: oriList){
//						pw.println(m.getKey()+"\t"+m.getValue());
//					}
//					pw.println();
//					int tmpNum3 = 0;
//					for(String n : list){
//						String word = n.split("\t")[0];
//						String key = (String) oriList.get(tmpNum3).getKey();
//						String value = (String) oriList.get(tmpNum3).getValue();
//						if(word.equals(key)){
//							pw1.println(n+value);
//							tmpNum3++;
//						} else if(word.contains(key)){
//							Set<String> labelSet = new LinkedHashSet<String>();
//							labelSet.add(value);
//							Boolean flag = true;
//							int len = key.length();
//							while(flag){
//								tmpNum3++;
//								if(len >= word.length()){
//									break;
//								} else {
//									key = (String) oriList.get(tmpNum3).getKey();
//									value = (String) oriList.get(tmpNum3).getValue();
//									if(!word.contains(key)){
//										flag = false;
//									} else {
//										labelSet.add(value);
//										len += key.length();
//									}
//								}
//							}
//							String label2 = "";
//							if(labelSet.size() == 1){
//								for(String q : labelSet){
//									label2 = q;
//								}
//							} else if(labelSet.contains("ID") && !labelSet.contains("TH")){
//								label2 = "ID";
//							} else if(labelSet.contains("TH")){
//								label2 = "TH";
//							} else if(labelSet.size() == 2){
//								if(labelSet.contains("OT")){
//									labelSet.remove("OT");
//									for(String q : labelSet){
//										label2 = q;
//									}
//								} else {
//									label2 = "UNKNOWN";
//								}
//							} else {
//								label2 = "UNKNOWN";
//							}
//							pw1.println(n+label2);
//						} else if(key.contains(word)){
//							pw1.println(n+value);
//						} else {
//							tmpNum3++;
//							key = (String) oriList.get(tmpNum3).getKey();
//							value = (String) oriList.get(tmpNum3).getValue();
//							if(word.contains(key)){
//								pw1.println(n+value);
//								tmpNum3++;
//							} else {
//								pw1.println(n+"UNKNOWN");
//							}
//						}
//					}
//					pw1.println();
                }
                //sent clear and romove
                list.clear();
                for (MyEntry<String, String> q : oriList) {
                    oriSent.remove(q);
                }
                if (!oriSent.isEmpty()) {
                    oriSent.remove(0);
                }
            }
        }
        br1.close();
        pw.close();
        pw1.close();
        System.out.println("\nall:" + flagall);
    }


}
