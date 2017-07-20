package com.onyx.android.dr.reader.event;

/**
 * Created by huxiaomao on 17/5/24.
 */

public class DropDownListViewItemClickEvent {
    private int position;

    public DropDownListViewItemClickEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
