package com.onyx.jdread.reader.catalog.event;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class AnnotationItemClickEvent {
    private String position;

    public AnnotationItemClickEvent(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
