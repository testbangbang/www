package com.onyx.android.dr.reader.data;

/**
 * Created by huxiaomao on 17/5/25.
 */

public class BookInfo {
    private String bookName;
    private String bookPath;
    private String password;

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
