package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 2017/9/28.
 */
public class ReadingRateBean {
    private String time;
    private String bookName;
    private String timeHorizon;
    private String languageType;
    private int readSummaryPiece;
    private int readerResponsePiece;
    private int readerResponseNumber;
    private String md5;
    private String type;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(String timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public int getReadSummaryPiece() {
        return readSummaryPiece;
    }

    public void setReadSummaryPiece(int readSummaryPiece) {
        this.readSummaryPiece = readSummaryPiece;
    }

    public int getReaderResponsePiece() {
        return readerResponsePiece;
    }

    public void setReaderResponsePiece(int readerResponsePiece) {
        this.readerResponsePiece = readerResponsePiece;
    }

    public int getReaderResponseNumber() {
        return readerResponseNumber;
    }

    public void setReaderResponseNumber(int readerResponseNumber) {
        this.readerResponseNumber = readerResponseNumber;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
