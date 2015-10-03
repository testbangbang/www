package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderView {

    /**
     * change view options, like margins
     * @param options
     */
    public void setViewOptions(final ReaderViewOptions options);

    /**
     * Navigate to next page.
     * @return
     */
    public boolean nextPage();

    /**
     * Navigate to prev page.
     * @return
     */
    public boolean prevPage();

    /**
     * Navigate to first page.
     * @return
     */
    public boolean firstPage();

    /**
     * Navigate to last page.
     * @return
     */
    public boolean lastPage();

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final ReaderDocumentPosition position);

    /**
     * query if the view supports text style.
     */
    public boolean supportTextStyle();

    /**
     * Change the text style
     * @param textStyle
     * @return
     */
    public boolean setTextStyle(final ReaderTextStyle textStyle);


    /**
     * Check if the view supports scaling or not.
     * @return
     */
    public boolean supportScaling();


    /**
     * Change the scaling style.
     * @param scalingStyle
     * @return
     */
    public boolean setScalingStyle(final ReaderScalingStyle scalingStyle);

    /**
     * Check if the view supports different page layout or not.
     * @return
     */
    public boolean supportPageLayout();

    /**
     * Change page layout.
     * @param pageLayout
     * @return
     */
    public boolean setPageLayout(final ReaderPageLayout pageLayout);


}
