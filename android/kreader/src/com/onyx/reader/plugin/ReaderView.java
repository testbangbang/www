package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderView {

    /**
     * change view options, like margins
     * @param options
     */
    public void changeViewOptions(final ReaderViewOptions options);

    /**
     *
     */

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




}
