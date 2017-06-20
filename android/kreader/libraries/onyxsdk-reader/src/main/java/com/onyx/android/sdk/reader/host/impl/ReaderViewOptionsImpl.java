package com.onyx.android.sdk.reader.host.impl;

import com.onyx.android.sdk.reader.api.ReaderViewOptions;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class ReaderViewOptionsImpl implements ReaderViewOptions {

    private int width;
    private int height;

    public ReaderViewOptionsImpl() {
    }

    public ReaderViewOptionsImpl(int w, int h) {
        width = w;
        height = h;
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

    public int getViewWidth() {
        return width;
    }

    public int getViewHeight() {
        return height;
    }


}
