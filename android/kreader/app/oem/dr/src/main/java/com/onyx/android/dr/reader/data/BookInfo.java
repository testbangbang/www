package com.onyx.android.dr.reader.data;

import com.onyx.android.sdk.data.model.DocumentInfo;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by huxiaomao on 17/5/25.
 */

public class BookInfo {
    private String bookName;
    private String bookPath;
    private String password;
    private String language;
    private DocumentInfo documentInfo;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public void setDocumentInfo(DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
    }
}
