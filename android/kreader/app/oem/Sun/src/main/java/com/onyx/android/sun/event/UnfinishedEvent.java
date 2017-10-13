package com.onyx.android.sun.event;

/**
 * Created by li on 2017/10/12.
 */

public class UnfinishedEvent {
    private String title;
    private String type;
    private int id;

    public UnfinishedEvent(int id, String type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
