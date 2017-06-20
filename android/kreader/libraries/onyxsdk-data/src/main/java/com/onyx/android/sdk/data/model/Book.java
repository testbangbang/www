package com.onyx.android.sdk.data.model;

import java.util.Date;

/**
 * Created by ming on 2017/2/15.
 */

public class Book {

    private String md5;
    private String md5short;

    private String name;
    private Date begin;
    private Date end;
    private long readingTime;

    private long annotation;
    private long lookupDic;
    private long textSelect;

    public long getAnnotation() {
        return annotation;
    }

    public void setAnnotation(long annotation) {
        this.annotation = annotation;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getTextSelect() {
        return textSelect;
    }

    public void setTextSelect(long textSelect) {
        this.textSelect = textSelect;
    }

    public long getLookupDic() {
        return lookupDic;
    }

    public void setLookupDic(long lookupDic) {
        this.lookupDic = lookupDic;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5short() {
        return md5short;
    }

    public void setMd5short(String md5short) {
        this.md5short = md5short;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public long getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(long readingTime) {
        this.readingTime = readingTime;
    }
}
