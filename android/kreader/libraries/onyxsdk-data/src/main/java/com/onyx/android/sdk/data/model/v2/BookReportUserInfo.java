package com.onyx.android.sdk.data.model.v2;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportUserInfo {
    private String _id;
    private String interest;
    private String textBooks;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getTextBooks() {
        return textBooks;
    }

    public void setTextBooks(String textBooks) {
        this.textBooks = textBooks;
    }
}
