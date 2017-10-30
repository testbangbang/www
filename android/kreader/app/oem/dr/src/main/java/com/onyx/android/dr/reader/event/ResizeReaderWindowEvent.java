package com.onyx.android.dr.reader.event;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class ResizeReaderWindowEvent {
    public int width;
    public int height;

    public ResizeReaderWindowEvent(int w, int h) {
        width = w;
        height = h;
    }
}
