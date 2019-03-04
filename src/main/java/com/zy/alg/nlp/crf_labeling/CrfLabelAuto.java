package com.zy.alg.nlp.crf_labeling;

import com.zy.alg.domain.Result;
import com.zy.alg.domain.Term;
import com.zy.alg.service.AnsjSeg;
import com.zy.alg.service.AnsjSegImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * crf模型自动指导标注
 *
 * @author zhangyuo
 */
public class CrfLabelAuto {

    public static void main(String argv[]) throws IOException {
        String corpusPath = "F:\\序列标注\\搜索属性识别\\已确认部分\\";
        //标注句子所有词被标注为TH属性
        allTh(corpusPath);
        //对标注句子属性为所有TH进行自动修改标注
        allThAutoLabel(corpusPath + "自动指导标注/");
        //标注句子所有词被标注为ID属性
        allId(corpusPath);
        //标注句子不含TH属性
        NOTH(corpusPath);
        //对标注句子属性为所有ID和没有TH进行自动修改标注
        allIdAndNoThAutoLabel(corpusPath);
    }

    private static void allIdAndNoThAutoLabel(String corpusPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData.final"), "utf-8"));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "IndWordTable"), "utf-8"));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "SerWordTable"), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.new"), "utf-8"), true);

        AnsjSeg ansjSeg = AnsjSegImpl.getSingleton();

        String line1 = null;
        Set<String> IndWord = new HashSet<String>();
        while ((line1 = br1.readLine()) != null) {
            IndWord.add(line1);
        }
        br1.close();

        String line2 = null;
        Set<String> SerWord = new HashSet<String>();
        while ((line2 = br2.readLine()) != null) {
            SerWord.add(line2);
        }
        br2.close();

        Set<String> filterIndWord = new HashSet<String>();
        for (String q : IndWord) {
            if (!SerWord.contains(q)) {
                Result word = ansjSeg.textTokenizer(q, "1");
                String segIndWord = "";
                for (Term t : word) {
                    segIndWord += t.getName() + "\t";
                }
                filterIndWord.add(segIndWord);
            }
        }

        Set<String> SegSerWord = new HashSet<String>();
        for (String q : SerWord) {
            Result word = ansjSeg.textTokenizer(q, "1");
            String segSerWord = "";
            for (Term t : word) {
                segSerWord += t.getName() + "\t";
            }
            SegSerWord.add(segSerWord);
        }

        String line = null;
        int num = 0;
        List<String> sentTh = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                sentTh.add(line);
            } else {
                String sentSegWord = "";
                for (String q : sentTh) {
                    String[] seg1 = q.split("\t");
                    sentSegWord += seg1[0] + "\t";
                }
                // industry word guide label
                for (String v : filterIndWord) {
                    if (sentSegWord.contains(v)) {
                        List<String> tmpsentId = new ArrayList<String>();
                        String sentSegWord1 = sentSegWord.replace("\t", "");
                        String v1 = v.replace("\t", "");
                        int i = sentSegWord1.indexOf(v1);
                        int j = v1.length();
                        int len = 0;
                        int len1 = 0;
                        for (String q : sentTh) {
                            String[] seg1 = q.split("\t");
                            len += seg1[0].length();
                            if (len <= i) {
                                tmpsentId.add(q);
                            } else {
                                len1 += seg1[0].length();
                                if (len1 <= j) {
                                    tmpsentId.add(seg1[0] + "\t" + seg1[1] + "\t" + "ID");
                                } else {
                                    tmpsentId.add(q);
                                }
                            }
                        }
                        sentTh = tmpsentId;
                    }
                }
                //service word guide label
                for (String v : SegSerWord) {
                    if (sentSegWord.contains(v)) {
                        List<String> tmpsentTh = new ArrayList<String>();
                        String sentSegWord1 = sentSegWord.replace("\t", "");
                        String v1 = v.replace("\t", "");
                        int i = sentSegWord1.indexOf(v1);
                        int j = v1.length();
                        int len = 0;
                        int len1 = 0;
                        for (String q : sentTh) {
                            String[] seg1 = q.split("\t");
                            len += seg1[0].length();
                            if (len <= i) {
                                tmpsentTh.add(q);
                            } else {
                                len1 += seg1[0].length();
                                if (len1 <= j) {
                                    tmpsentTh.add(seg1[0] + "\t" + seg1[1] + "\t" + "TH");
                                } else {
                                    tmpsentTh.add(q);
                                }
                            }
                        }
                        sentTh = tmpsentTh;
                    }
                }
//				Set<String> set = new HashSet<String>();
//				if(sentTh.size() ==2
//						&& sentTh.get(0).split("\t")[2].equals("TH")
//						&& sentTh.get(1).split("\t")[2].equals("TH")){
//					set.add(sentTh.get(0).split("\t")[1]);
//					set.add(sentTh.get(1).split("\t")[1]);
//					if((set.contains("n") && set.contains("v"))
//							|| (set.contains("n") && set.contains("vn"))
//							|| (set.contains("nz") && set.contains("v"))
//							|| (set.contains("nz") && set.contains("vn"))
//							|| (set.contains("zbj") && set.contains("vn"))
//							|| (set.contains("zbj") && set.contains("v"))){
//						List<String> tmpsentTh = new ArrayList<String>();
//						for(String q : sentTh){
//							String[] seg1 = q.split("\t");
//							if((seg1[1].equals("v") || seg1[1].equals("vn"))
//									&& !SerWord.contains(seg1[0])){
//								tmpsentTh.add(seg1[0] + "\t" + seg1[1] + "\t" + "DE");
//							} else {
//								tmpsentTh.add(q);
//							}
//						}
//						sentTh = tmpsentTh;
//					}
//				}
                // output
                for (String q : sentTh) {
                    pw.println(q);
                }
                pw.println();
                sentTh.clear();
            }
        }
        br.close();
        pw.close();
    }

    private static void NOTH(String corpusPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData.final"), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.NOTH"), "utf-8"), true);
        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.new"), "utf-8"), true);
        String line = null;
        int num = 0;
        Set<String> setTh = new HashSet<String>();
        Set<String> setLine = new HashSet<String>();
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                setTh.add(seg[2]);
                setLine.add(line);
            } else {
                if (!setTh.contains("TH")) {
                    for (String q : setLine) {
                        pw.println(q);
                    }
                    pw.println();
                } else {
                    for (String q : setLine) {
                        pw1.println(q);
                    }
                    pw1.println();
                }
                setTh.clear();
                setLine.clear();
            }
        }
        br.close();
        pw.close();
        pw1.close();
    }

    private static void allId(String corpusPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData.final"), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.ID"), "utf-8"), true);
        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.new"), "utf-8"), true);
        String line = null;
        int num = 0;
        Set<String> setTh = new HashSet<String>();
        Set<String> setLine = new HashSet<String>();
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                setTh.add(seg[2]);
                setLine.add(line);
            } else {
                if (setTh.size() == 1 && setTh.contains("ID")) {
                    for (String q : setLine) {
                        pw.println(q);
                    }
                    pw.println();
                } else {
                    for (String q : setLine) {
                        pw1.println(q);
                    }
                    pw1.println();
                }
                setTh.clear();
                setLine.clear();
            }
        }
        br.close();
        pw.close();
        pw1.close();
    }

    private static void allThAutoLabel(String corpusPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData.final.TH"), "utf-8"));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "IndWordTable"), "utf-8"));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "SerWordTable"), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.TH.new"), "utf-8"), true);

        AnsjSeg ansjSeg = AnsjSegImpl.getSingleton();

        String line1 = null;
        Set<String> IndWord = new HashSet<String>();
        while ((line1 = br1.readLine()) != null) {
            IndWord.add(line1);
        }
        br1.close();

        String line2 = null;
        Set<String> indWordsSiSer = new HashSet<String>();
        while ((line2 = br2.readLine()) != null) {
            indWordsSiSer.add(line2);
        }
        br2.close();

        Set<String> filterIndWord = new HashSet<String>();
        for (String q : IndWord) {
            if (!indWordsSiSer.contains(q)) {
                Result word = ansjSeg.textTokenizer(q, "1");
                String segIndWord = "";
                for (Term t : word) {
                    segIndWord += t.getName() + "\t";
                }
                filterIndWord.add(segIndWord);
            }
        }

        String line = null;
        int num = 0;
        List<String> sentTh = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                sentTh.add(line);
            } else {
                String sentSegWord = "";
                for (String q : sentTh) {
                    String[] seg1 = q.split("\t");
                    sentSegWord += seg1[0] + "\t";
                }
                for (String v : filterIndWord) {
                    if (sentSegWord.contains(v)) {
                        List<String> tmpsentTh = new ArrayList<String>();
                        String sentSegWord1 = sentSegWord.replace("\t", "");
                        String v1 = v.replace("\t", "");
                        int i = sentSegWord1.indexOf(v1);
                        int j = v1.length();
                        int len = 0;
                        int len1 = 0;
                        for (String q : sentTh) {
                            String[] seg1 = q.split("\t");
                            len += seg1[0].length();
                            if (len <= i) {
                                tmpsentTh.add(q);
                            } else {
                                len1 += seg1[0].length();
                                if (len1 <= j) {
                                    tmpsentTh.add(seg1[0] + "\t" + seg1[1] + "\t" + "ID");
                                } else {
                                    tmpsentTh.add(q);
                                }
                            }
                        }
                        sentTh = tmpsentTh;
                    }
                }
                Set<String> set = new HashSet<String>();
                if (sentTh.size() == 2
                        && sentTh.get(0).split("\t")[2].equals("TH")
                        && sentTh.get(1).split("\t")[2].equals("TH")) {
                    set.add(sentTh.get(0).split("\t")[1]);
                    set.add(sentTh.get(1).split("\t")[1]);
                    if ((set.contains("n") && set.contains("v"))
                            || (set.contains("n") && set.contains("vn"))
                            || (set.contains("nz") && set.contains("v"))
                            || (set.contains("nz") && set.contains("vn"))
                            || (set.contains("zbj") && set.contains("vn"))
                            || (set.contains("zbj") && set.contains("v"))) {
                        List<String> tmpsentTh = new ArrayList<String>();
                        for (String q : sentTh) {
                            String[] seg1 = q.split("\t");
                            if ((seg1[1].equals("v") || seg1[1].equals("vn"))
                                    && !indWordsSiSer.contains(seg1[0])) {
                                tmpsentTh.add(seg1[0] + "\t" + seg1[1] + "\t" + "DE");
                            } else {
                                tmpsentTh.add(q);
                            }
                        }
                        sentTh = tmpsentTh;
                    }
                }
                // output
                for (String q : sentTh) {
                    pw.println(q);
                }
                pw.println();
                sentTh.clear();
            }
        }
        br.close();
        pw.close();
    }

    private static void allTh(String corpusPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(corpusPath + "crftrainData.final"), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.TH"), "utf-8"), true);
        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(corpusPath + "crftrainData.final.new"), "utf-8"), true);
        String line = null;
        int num = 0;
        Set<String> setTh = new HashSet<String>();
        Set<String> setLine = new HashSet<String>();
        while ((line = br.readLine()) != null) {
            System.out.println(++num);
            String[] seg = line.split("\t");
            if (seg.length == 3) {
                setTh.add(seg[2]);
                setLine.add(line);
            } else {
                if (setTh.size() == 1 && setTh.contains("TH")) {
                    for (String q : setLine) {
                        pw.println(q);
                    }
                    pw.println();
                } else {
                    for (String q : setLine) {
                        pw1.println(q);
                    }
                    pw1.println();
                }
                setTh.clear();
                setLine.clear();
            }
        }
        br.close();
        pw.close();
        pw1.close();
    }

}
