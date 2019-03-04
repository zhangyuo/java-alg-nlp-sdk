package com.zy.alg.nlp.demo;

import java.io.IOException;
import java.util.Map;

import com.zy.alg.domain.Term;
import com.zy.alg.nlp.crf.ModelParse;
import com.zy.alg.nlp.server.CRF;
import com.zy.alg.nlp.server.CRFEnhancer;
import com.zy.alg.nlp.utils.CrfInfo;
import com.zy.alg.service.AnsjSeg;
import com.zy.alg.service.AnsjSegImpl;
import com.zy.alg.util.LoadDic;
import org.nlpcn.commons.lang.tire.domain.Forest;

public class DemoCRF {

    public static void main(String[] args) throws IOException {

        String resourcePath = "G:\\project\\nlp\\model\\";
        String ZBJDicPath = resourcePath + "zbjsmall.dic";
        String CRFmodelRealpath = resourcePath + "crfmodel.txt";
        AnsjSeg ansjSeg = AnsjSegImpl.getSingleton();
        Forest zbjdic = new Forest();
        LoadDic.insertUserDefineDic(zbjdic, ZBJDicPath);
        ModelParse MP = new ModelParse();
        MP.parse(CRFmodelRealpath);
        CRF crf = new CRFEnhancer();

        String oriLabel = "我需要一个logo设计";
        // 最优路径
        System.out.println("最优路径:");
        Map<Term, String> centerword = crf.getWordLabel(oriLabel, zbjdic, MP);
        for (Map.Entry<Term, String> t : centerword.entrySet()) {
            System.out.println(t.getKey() + "\t" + t.getValue());
        }
        // 最优路径分数
        System.out.println("最优路径分数:");
        Map<Term, CrfInfo> centerword1 = crf.getLabelScore(oriLabel, zbjdic, MP);
        for (Map.Entry<Term, CrfInfo> t : centerword1.entrySet()) {
            System.out.println(t.getKey() + "\t" + t.getValue().getWordLabel() + "\t" + t.getValue().getLabelScore());
        }
    }

}
