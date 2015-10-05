package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 * * Single page
 * * Reflow page
 * * Continuous page
 */
public interface ReaderPageLayoutManager {

    /**
     * Set single page layout.
     */
    public void setSinglePageLayout();

    public boolean isSinglePageLayout();

    /**
     * Set continuous page layout.
     */
    public void setContinuousPageLayout();

    public boolean isContinuousPageLayout();

    /**
     * Set reflow layout.
     */
    public void setReflowLayout();

    public boolean isReflowLayout();

}
