package com.zy.alg.nlp.utils;

public class CrfInfo {

    /**
     * 最优路径标签
     */
    private String wordLabel;
    /**
     * 最优路径标签本身权重分数
     */
    private double labelScore;

    public String getWordLabel() {
        return wordLabel;
    }

    public void setWordLabel(String wordLabel) {
        this.wordLabel = wordLabel;
    }

    public double getLabelScore() {
        return labelScore;
    }

    public void setLabelScore(double labelScore) {
        this.labelScore = labelScore;
    }

    @Override
    public String toString() {
        return wordLabel + "=" + labelScore;
    }

}
