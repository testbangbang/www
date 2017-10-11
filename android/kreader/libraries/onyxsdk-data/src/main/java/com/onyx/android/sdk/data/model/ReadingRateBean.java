package com.onyx.android.sdk.data.model;

import java.util.Date;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class ReadingRateBean {
    public Date recordDate;
    public String name;
    public String book;
    public String readTimeLong;
    public int wordsCount;
    public String language;
    public int speed;
    public int summaryCount;
    public int impressionCount;
    public int impressionWordsCount;

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getReadTimeLong() {
        return readTimeLong;
    }

    public void setReadTimeLong(String readTimeLong) {
        this.readTimeLong = readTimeLong;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSummaryCount() {
        return summaryCount;
    }

    public void setSummaryCount(int summaryCount) {
        this.summaryCount = summaryCount;
    }

    public int getImpressionCount() {
        return impressionCount;
    }

    public void setImpressionCount(int impressionCount) {
        this.impressionCount = impressionCount;
    }

    public int getImpressionWordsCount() {
        return impressionWordsCount;
    }

    public void setImpressionWordsCount(int impressionWordsCount) {
        this.impressionWordsCount = impressionWordsCount;
    }
}
