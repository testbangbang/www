package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class GoodSentenceBean {
    private String details;
    private String readingMatter;
    private String pageNumber;

    public GoodSentenceBean(String details, String readingMatter, String pageNumber) {
        this.details = details;
        this.readingMatter = readingMatter;
        this.pageNumber = pageNumber;
    }

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
}
