package com.onyx.download.onyxdownloadservice;

/**
 * Created by 12 on 2017/1/21.
 */

public class InfoEvent {
    public int reference;
    public int state;
    public long finished;
    public long total;

    public InfoEvent(int reference, int state, long finished, long total) {
        this.reference = reference;
        this.state = state;
        this.finished = finished;
        this.total = total;
    }
}
