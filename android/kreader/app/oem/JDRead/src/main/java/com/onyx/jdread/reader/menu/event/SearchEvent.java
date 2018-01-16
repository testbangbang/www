package com.onyx.jdread.reader.menu.event;

/**
 * Created by li on 2018/1/13.
 */

public class SearchEvent {
    private String content;

    public SearchEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
