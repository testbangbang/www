package com.onyx.jdread.reader.layout;


/**
 * Created by zhuzeng on 2/9/16.
 */
public abstract class LayoutManagerCallback {

    public abstract int getViewWidth();

    public abstract int getViewHeight();

    public abstract String getInitPosition();

    public abstract boolean supportScale();

    public abstract boolean supportFontSizeAdjustment();
}
