package com.onyx.jdread.reader.catalog.event;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class BookmarkItemClickEvent {
    private String position;

    public BookmarkItemClickEvent(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
