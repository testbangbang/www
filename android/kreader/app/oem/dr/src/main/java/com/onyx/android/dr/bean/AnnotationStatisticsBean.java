package com.onyx.android.dr.bean;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.Date;

/**
 * Created by hehai on 17-9-28.
 */

public class AnnotationStatisticsBean {
    private Date time;
    private Metadata book;
    private Library library;
    private String idString;
    private int count;
    private boolean checked;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Metadata getBook() {
        return book;
    }

    public void setBook(Metadata book) {
        this.book = book;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }
}
