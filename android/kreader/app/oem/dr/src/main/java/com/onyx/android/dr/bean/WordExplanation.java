package com.onyx.android.dr.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 15/12/30 17:46.
 */
public class WordExplanation {
    public String getPhoneticSymbol() {
        return phoneticSymbol;
    }

    public void setPhoneticSymbol(String phoneticSymbol) {
        this.phoneticSymbol = phoneticSymbol;
    }

    public List<ExplanationInfo> getExplanationList() {
        return explanationList;
    }

    public void setExplanationList(List<ExplanationInfo> explanationList) {
        this.explanationList = explanationList;
    }

    String phoneticSymbol = "";
    List<ExplanationInfo> explanationList = new ArrayList<ExplanationInfo>();
}
