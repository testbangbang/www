package com.onyx.android.sun.event;

/**
 * Created by jackdeng on 2017/10/27.
 */

public class ToStudyReportDeatilEvent {

    private int id;
    private String title;

    public ToStudyReportDeatilEvent(int id,String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
