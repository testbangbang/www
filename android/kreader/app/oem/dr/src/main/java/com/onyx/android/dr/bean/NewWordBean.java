package com.onyx.android.dr.bean;

import java.io.Serializable;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class NewWordBean implements Serializable {
    private String newWord;
    private String dictionaryLookup;
    private String readingMatter;
    private String pageNumber;
    private int newWordType;
    private boolean tag;

    public String getDictionaryLookup() {
        return dictionaryLookup;
    }

    public void setDictionaryLookup(String dictionaryLookup) {
        this.dictionaryLookup = dictionaryLookup;
    }

    public String getReadingMatter() {
        return readingMatter;
    }

    public void setReadingMatter(String readingMatter) {
        this.readingMatter = readingMatter;
    }

    public String getNewWord() {
        return newWord;
    }

    public void setNewWord(String newWord) {
        this.newWord = newWord;
    }

    public int getNewWordType() {
        return newWordType;
    }

    public void setNewWordType(int newWordType) {
        this.newWordType = newWordType;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}
