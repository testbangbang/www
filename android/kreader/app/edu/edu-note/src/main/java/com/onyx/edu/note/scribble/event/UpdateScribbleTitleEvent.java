package com.onyx.edu.note.scribble.event;

/**
 * Created by lxm on 2017/9/7.
 */

public class UpdateScribbleTitleEvent {

    private String title;

    public String getTitle() {
        return title;
    }

    public UpdateScribbleTitleEvent(String title) {
        this.title = title;
    }
}
