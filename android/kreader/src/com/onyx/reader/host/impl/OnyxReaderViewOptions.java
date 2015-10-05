package com.onyx.reader.host.impl;

import com.onyx.reader.plugin.ReaderViewOptions;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OnyxReaderViewOptions implements ReaderViewOptions {

    private int width;
    private int height;

    public int getViewWidth() {
        return width;
    }

    public int getViewHeight() {
        return height;
    }

    public int getTopMargin(){
        return 0;
    }

    public int getLeftMargin() {
        return 0;
    }

    public int getBottomMargin() {
        return 0;
    }

    public int getRightMargin() {
        return 0;
    }

}
