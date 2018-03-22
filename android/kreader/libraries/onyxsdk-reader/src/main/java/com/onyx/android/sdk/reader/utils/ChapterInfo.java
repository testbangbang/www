package com.onyx.android.sdk.reader.utils;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class ChapterInfo {
    private String title;
    private String position;

    public ChapterInfo(String title, String position) {
        this.title = title;
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }
}
