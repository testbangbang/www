package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/3/15.
 * * Single page
 * * Reflow page
 * * Continuous page
 */
public interface ReaderPageLayout {

    public void setSinglePageLayout();

    public boolean isSinglePageLayout();

    public void setContinuousPageLayout();

    public boolean isContinuousPageLayout();

    public void setReflowLayout();

    public boolean isReflowLayout();

}
