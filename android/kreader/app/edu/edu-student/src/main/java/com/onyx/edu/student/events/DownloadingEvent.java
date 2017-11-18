package com.onyx.edu.student.events;

/**
 * Created by suicheng on 2017/9/4.
 */
public class DownloadingEvent {
    public Object tag;
    public int progress;

    public DownloadingEvent() {
    }

    public DownloadingEvent(Object tag, int progress) {
        this.tag = tag;
        this.progress = progress;
    }
}
