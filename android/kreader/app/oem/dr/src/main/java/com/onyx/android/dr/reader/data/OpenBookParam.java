package com.onyx.android.dr.reader.data;


/**
 * Created by huxiaomao on 17/5/26.
 */

public class OpenBookParam {
    private String password;
    private String localPath;
    private String bookName;
    private String bookId;
    private boolean isFluent;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFluent() {
        return isFluent;
    }

    public void setFluent(boolean fluent) {
        isFluent = fluent;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
