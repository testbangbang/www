package com.onyx.android.sdk.reader.utils;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class ChapterInfo {
    private String title;
    private int position;

    public ChapterInfo(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
