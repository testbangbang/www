package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 * * Single page
 * * Reflow page
 * * Continuous page
 */
public interface ReaderPageLayoutManager {

    /**
     * Check if plugin supports single page layout or not.
     * @return
     */
    public boolean supportSinglePageLayout();

    /**
     * Set single page layout. In single page layout, all pages are arranged
     * page by page.
     */
    public void setSinglePageLayout();

    /**
     * Check if current page layout is single page layout.
     * @return true if in single page layout.
     */
    public boolean isSinglePageLayout();

    /**
     * Check if plugin supports continuous page layout or not.
     * @return
     */
    public boolean supportContinuousPageLayout();

    /**
     * Set continuous page layout. In continuous layout, all pages are arranged in
     * scrolling mode. User can scroll pages.
     */
    public void setContinuousPageLayout();

    /**
     * Check if the page layout is continuous or not.
     * @return true if in continuous mode.
     */
    public boolean isContinuousPageLayout();

    /**
     * Support reflow layout or not.
     * @return true if supports.
     */
    public boolean supportReflowLayout();

    /**
     * Set reflow layout.
     */
    public void setReflowLayout();

    /**
     * When in reflow layout
     * @return
     */
    public boolean isReflowLayout();

}
