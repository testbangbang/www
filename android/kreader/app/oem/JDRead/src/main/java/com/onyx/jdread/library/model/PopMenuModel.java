package com.onyx.jdread.library.model;

/**
 * Created by hehai on 17-12-21.
 */

public class PopMenuModel {
    private String text;

    private Object event;

    public PopMenuModel(String text, Object event) {
        this.text = text;
        this.event = event;
    }

    public String getText() {
        return text;
    }

    public Object getEvent() {
        return event;
    }
}
