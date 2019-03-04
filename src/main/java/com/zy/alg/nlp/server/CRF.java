package com.zy.alg.nlp.server;

import com.zy.alg.domain.Term;
import org.nlpcn.commons.lang.tire.domain.Forest;

import com.zy.alg.nlp.crf.ModelParse;
import com.zy.alg.nlp.utils.CrfInfo;

import java.util.Map;

public interface CRF {

    /**
     * crf analysis: get word label
     *
     * @param query
     * @param userdic
     * @param MP
     * @return queryAttribute
     * @attribute TH-theme	ID-industry DE-Demand LA-location ST-style OT-other
     */
    public Map<Term, String> getWordLabel(String query, Forest userdic, ModelParse MP);

    /**
     * crf analysis: get word label and word itself weigtht
     *
     * @param query
     * @param userdic
     * @param MP
     * @return queryAttribute and path score
     * @attribute TH-theme	ID-industry DE-Demand LA-location ST-style OT-other
     */
    public Map<Term, CrfInfo> getLabelScore(String query, Forest userdic, ModelParse MP);

    /**
     * crf analysis: get word label————com.zbj.alg.seg分词中已添加默认zbjsmall.dic
     *
     * @param query
     * @param MP
     * @return
     */
    public Map<Term, String> getWordLabel(String query, ModelParse MP);

    /**
     * crf analysis: get word label and word itself weigtht————com.zbj.alg.seg分词中已添加默认zbjsmall.dic
     *
     * @param query
     * @param MP
     * @return
     */
    public Map<Term, CrfInfo> getLabelScore(String query, ModelParse MP);

}
