package com.onyx.edu.reader.ui.events;

/**
 * Created by ming on 2017/4/27.
 */

public class UpdatePagePositionEvent {

    private int position;
    private int total;

    public UpdatePagePositionEvent(int position, int total) {
        this.position = position;
        this.total = total;
    }

    public int getPosition() {
        return position;
    }

    public int getTotal() {
        return total;
    }
}
