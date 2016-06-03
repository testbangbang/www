package com.onyx.kreader.dataprovider;

import android.graphics.Rect;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/3/16.
 */
@Table(database = ReaderDatabase.class)
public class Annotation extends BaseData {

    @Column
    private String quote = null;

    @Column
    private String locationBegin = null;

    @Column
    private String locationEnd = null;

    @Column
    private String note = null;

    @Column
    private String application = null;

    @Column
    private String position = null;

//    @Column
    private List<Rect> rects = new ArrayList<Rect>();

    public void setQuote(final String q) {
        quote = q;
    }

    public String getQuote() {
        return quote;
    }

    public void setLocationBegin(final String l) {
        locationBegin = l;
    }

    public String getLocationBegin() {
        return locationBegin;
    }

    public void setLocationEnd(final String l) {
        locationEnd = l;
    }

    public String getLocationEnd() {
        return locationEnd;
    }

    public void setNote(final String n) {
        note = n;
    }

    public final String getNote() {
        return note;
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

    public void setRects(final List<Rect> rectList) {
        rects.addAll(rectList);
    }

    public List<Rect> getRects() {
        return rects;
    }
}

