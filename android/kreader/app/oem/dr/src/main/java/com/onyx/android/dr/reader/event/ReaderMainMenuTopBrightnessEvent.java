package com.onyx.android.dr.reader.event;

import android.view.View;

/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderMainMenuTopBrightnessEvent {
    private int offset;
    private View view;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
