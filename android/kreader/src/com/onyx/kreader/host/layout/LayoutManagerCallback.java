package com.onyx.kreader.host.layout;

import com.onyx.kreader.api.ReaderPagePosition;

/**
 * Created by zhuzeng on 2/9/16.
 */
public abstract class LayoutManagerCallback {

    public abstract int getViewWidth();

    public abstract int getViewHeight();

    public abstract ReaderPagePosition getInitPosition();

    public abstract boolean supportScale();

    public abstract boolean supportFontSizeAdjustment();
}
