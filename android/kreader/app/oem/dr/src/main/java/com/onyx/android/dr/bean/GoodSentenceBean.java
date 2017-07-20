package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class GoodSentenceBean {
    private String details;
    private String readingMatter;
    private String pageNumber;
    private int goodSentenceType;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getReadingMatter() {
        return readingMatter;
    }

    public void setReadingMatter(String readingMatter) {
        this.readingMatter = readingMatter;
    }

    public int getGoodSentenceType() {
        return goodSentenceType;
    }

    public void setGoodSentenceType(int goodSentenceType) {
        this.goodSentenceType = goodSentenceType;
    }
}
