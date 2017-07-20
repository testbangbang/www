package com.onyx.android.dr.reader.event;

/**
 * Created by huxiaomao on 17/5/18.
 */

public class DeleteBookmarkResultEvent {
    private int position;

    public int getPosition() {
        return position;
    }

    public DeleteBookmarkResultEvent setPosition(int position) {
        this.position = position;
        return this;
    }
}
