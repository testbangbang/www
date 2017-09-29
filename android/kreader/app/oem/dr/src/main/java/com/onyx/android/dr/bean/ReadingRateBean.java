package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 2017/9/28.
 */
public class ReadingRateBean {
    private String time;
    private String bookName;
    private String timeHorizon;
    private String languageType;
    private String readSummaryPiece;
    private String readerResponsePiece;
    private String readerResponseNumber;
    private String md5;

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

    public String getReadSummaryPiece() {
        return readSummaryPiece;
    }

    public void setReadSummaryPiece(String readSummaryPiece) {
        this.readSummaryPiece = readSummaryPiece;
    }

    public String getReaderResponsePiece() {
        return readerResponsePiece;
    }

    public void setReaderResponsePiece(String readerResponsePiece) {
        this.readerResponsePiece = readerResponsePiece;
    }

    public String getReaderResponseNumber() {
        return readerResponseNumber;
    }

    public void setReaderResponseNumber(String readerResponseNumber) {
        this.readerResponseNumber = readerResponseNumber;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
