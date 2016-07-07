package com.onyx.kreader.dataprovider;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 6/3/16.
 */
@Table(database = ReaderDatabase.class)
public class Bookmark extends BaseData {

    @Column
    private String quote = null;

    @Column
    private String application = null;

    @Column
    private String position = null;

    public void setQuote(final String q) {
        quote = q;
    }

    public String getQuote() {
        return quote;
    }

    public void setApplication(final String app) {
        application = app;
    }

    public String getApplication() {
        return application;
    }

    public void setPosition(final String p) {
        position = p;
    }

    public String getPosition() {
        return position;
    }
}
