package com.onyx.reader.api;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderNavigator {

    /**
     * Retrieve the default init position.
     * @return
     */
    public ReaderDocumentPosition getInitPosition();

    /**
     * Retrieve current position of screen beginning.
     * @return Current position.
     */
    public ReaderDocumentPosition getVisibleBeginningPosition();

    /**
     * Retrieve visible pages on screen.
      * @return the visible page list.
     */
    public List<ReaderDocumentPosition> getVisiblePages();

    /**
     * Get position from page number
     * @param pageNumber The 0 based page number.
     * @return
     */
    public ReaderDocumentPosition getPositionByPageNumber(int pageNumber);

    /**
     * Get position from page name.
     * @param name The page name.
     * @return
     */
    public ReaderDocumentPosition getPositionByPageName(final String name);


    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPageNumber();

    /**
     * Navigate to next screen.
     */
    public boolean nextScreen();

    /**
     * Navigate to prev screen.
     */
    public boolean prevScreen();

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
     * Retrieve current visible links.
     * @return
     */
    public List<ReaderLink> getVisibleLinks();


}
