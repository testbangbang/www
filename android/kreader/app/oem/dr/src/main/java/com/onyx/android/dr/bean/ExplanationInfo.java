package com.onyx.android.dr.bean;

/**
 * Created by solskjaer49 on 15/12/30 17:42.
 */
public class ExplanationInfo {
    public String getPartOfSpeechPattern() {
        return partOfSpeechPattern;
    }

    public void setPartOfSpeechPattern(String partOfSpeechPattern) {
        this.partOfSpeechPattern = partOfSpeechPattern;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    String partOfSpeechPattern;
    String explanation;
}
