package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class NewWordBean {
    private String newWord;
    private String dictionaryLookup;
    private String readingMatter;

    public NewWordBean(String newWord, String dictionaryLookup, String readingMatter) {
        this.newWord = newWord;
        this.dictionaryLookup = dictionaryLookup;
        this.readingMatter = readingMatter;
    }

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
}
