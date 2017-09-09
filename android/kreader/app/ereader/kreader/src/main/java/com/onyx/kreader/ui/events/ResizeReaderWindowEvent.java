package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class ResizeReaderWindowEvent {
    public int gravity;
    public int width;
    public int height;

    public ResizeReaderWindowEvent(int g, int w, int h) {
        gravity = g;
        width = w;
        height = h;
    }
}
