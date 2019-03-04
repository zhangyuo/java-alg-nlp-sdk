package com.zy.alg.nlp.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.zy.alg.domain.Result;
import com.zy.alg.domain.Term;
import com.zy.alg.library.UserDefineLibrary;
import com.zy.alg.splitword.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;

import com.zy.alg.nlp.crf.ModelParse;
import com.zy.alg.nlp.utils.CrfInfo;

/**
 * @author zhangyu
 * @date 2017/04/01
 */
public class CRFEnhancer implements CRF {

    @Override
    public Map<Term, String> getWordLabel(String query, Forest userdic, ModelParse MP) {
        Map<Term, String> queryAttribute = new LinkedHashMap<Term, String>();

        List<String> queryNature = new ArrayList<String>();
        String newquery = query.replaceAll("\\s*|\t|\r|\n", "");
        // 输入为空或全为特殊字符
        if (newquery.equals("")) {
            return queryAttribute;
        }
        Result parse = ToAnalysis.parse(newquery, UserDefineLibrary.FOREST, userdic);
        for (Term t : parse) {
            queryNature.add(t.getName() + "&&" + t.getNatureStr());
        }
        // 对query分词后的各个词打标签
        String labelstr = MP.useTestViterbi(queryNature);
        // 标签集为空
        if (labelstr.equals("")) {
            return queryAttribute;
        }

        if (labelstr.split("##").length == 2) {
            String[] all = labelstr.split("##");
            String[] labels = all[0].split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    queryAttribute.put(term, labels[i]);
                }
            }
        } else {
            String[] labels = labelstr.split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    queryAttribute.put(term, labels[i]);
                }
            }
        }

        return queryAttribute;
    }

    @Override
    public Map<Term, CrfInfo> getLabelScore(String query, Forest userdic, ModelParse MP) {
        Map<Term, CrfInfo> queryAttribute = new LinkedHashMap<Term, CrfInfo>();

        List<String> queryNature = new ArrayList<String>();
        String newquery = query.replaceAll("\\s*|\t|\r|\n", "");
        if (newquery.equals("")) {
            return queryAttribute;
        }
        Result parse = ToAnalysis.parse(newquery, UserDefineLibrary.FOREST, userdic);
        for (Term t : parse) {
            queryNature.add(t.getName() + "&&" + t.getNatureStr());
        }
        String labelstr = MP.useTestViterbi(queryNature);
        if (labelstr.equals("")) {
            return queryAttribute;
        }

        if (labelstr.split("##").length == 2) {
            String[] all = labelstr.split("##");
            String[] labels = all[0].split("\t");
            String[] scores = all[1].split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    CrfInfo crfInfo = new CrfInfo();
                    crfInfo.setWordLabel(labels[i]);
                    crfInfo.setLabelScore(Double.parseDouble(scores[i]));
                    queryAttribute.put(term, crfInfo);
                }
            }
        } else {
            String[] labels = labelstr.split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    CrfInfo crfInfo = new CrfInfo();
                    crfInfo.setWordLabel(labels[i]);
                    crfInfo.setLabelScore(Double.parseDouble("0"));
                    queryAttribute.put(term, crfInfo);
                }
            }
        }

        return queryAttribute;
    }

    @Override
    public Map<Term, String> getWordLabel(String query, ModelParse MP) {
        Map<Term, String> queryAttribute = new LinkedHashMap<Term, String>();

        List<String> queryNature = new ArrayList<String>();
        String newquery = query.replaceAll("\\s*|\t|\r|\n", "");
        if (newquery.equals("")) {
            return queryAttribute;
        }
        Result parse = ToAnalysis.parse(newquery);
        for (Term t : parse) {
            queryNature.add(t.getName() + "&&" + t.getNatureStr());
        }
        String labelstr = MP.useTestViterbi(queryNature);
        if (labelstr.equals("")) {
            return queryAttribute;
        }

        if (labelstr.split("##").length == 2) {
            String[] all = labelstr.split("##");
            String[] labels = all[0].split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    queryAttribute.put(term, labels[i]);
                }
            }
        } else {
            String[] labels = labelstr.split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    queryAttribute.put(term, labels[i]);
                }
            }
        }

        return queryAttribute;
    }

    @Override
    public Map<Term, CrfInfo> getLabelScore(String query, ModelParse MP) {
        Map<Term, CrfInfo> queryAttribute = new LinkedHashMap<Term, CrfInfo>();

        List<String> queryNature = new ArrayList<String>();
        String newquery = query.replaceAll("\\s*|\t|\r|\n", "");
        if (newquery.equals("")) {
            return queryAttribute;
        }
        Result parse = ToAnalysis.parse(newquery);
        for (Term t : parse) {
            queryNature.add(t.getName() + "&&" + t.getNatureStr());
        }
        String labelstr = MP.useTestViterbi(queryNature);
        if (labelstr.equals("")) {
            return queryAttribute;
        }

        if (labelstr.split("##").length == 2) {
            String[] all = labelstr.split("##");
            String[] labels = all[0].split("\t");
            String[] scores = all[1].split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    CrfInfo crfInfo = new CrfInfo();
                    crfInfo.setWordLabel(labels[i]);
                    crfInfo.setLabelScore(Double.parseDouble(scores[i]));
                    queryAttribute.put(term, crfInfo);
                }
            }
        } else {
            String[] labels = labelstr.split("\t");
            for (int i = 0, length = labels.length; i < length; i++) {
                Term term = parse.get(i);
                if (!queryAttribute.containsKey(term)) {
                    CrfInfo crfInfo = new CrfInfo();
                    crfInfo.setWordLabel(labels[i]);
                    crfInfo.setLabelScore(Double.parseDouble("0"));
                    queryAttribute.put(term, crfInfo);
                }
            }
        }

        return queryAttribute;
    }

}
