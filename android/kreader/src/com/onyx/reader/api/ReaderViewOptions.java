package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/2/15.
 * Defined in host and used by plugin.
 */
public interface ReaderViewOptions {

    /**
     * Retrive view width.
     * @return the view width.
     */
    public int getViewWidth();

    public int getViewHeight();

    public int getTopMargin();

    public int getLeftMargin();

    public int getBottomMargin();

    public int getRightMargin();


}
