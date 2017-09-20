package com.onyx.android.dr.reader.event;

/**
 * Created by hehai on 17-8-10.
 */

public class ReadingSummaryMenuEvent {
    private String bookName;
    private String pageNumber;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }
}
