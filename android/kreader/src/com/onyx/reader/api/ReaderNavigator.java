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
     * Get position from page number. Page position can be retrieved by both index and name.
     * @param pageNumber The 0 based page number.
     * @return
     */
    public ReaderDocumentPosition getPositionByPageNumber(int pageNumber);

    /**
     * Create position from persistent string.
     * @param string The persistent string.
     * @return
     */
    public ReaderDocumentPosition createPositionFromString(final String string);

    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage();

    /**
     * Navigate to next screen.
     */
    public ReaderDocumentPosition nextScreen(final ReaderDocumentPosition position);

    /**
     * Navigate to previous screen.
     */
    public ReaderDocumentPosition prevScreen(final ReaderDocumentPosition position);

    /**
     * Navigate to next page.
     * @return
     */
    public ReaderDocumentPosition nextPage(final ReaderDocumentPosition position);

    /**
     * Navigate to previous page.
     * @return
     */
    public ReaderDocumentPosition prevPage(final ReaderDocumentPosition position);

    /**
     * Navigate to first page.
     * @return
     */
    public ReaderDocumentPosition firstPage();

    /**
     * Navigate to last page.
     * @return
     */
    public ReaderDocumentPosition lastPage();

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final ReaderDocumentPosition position);

    /**
     * Retrieve current visible links.
     * @return
     */
    public List<ReaderLink> getLinks(final ReaderDocumentPosition position);


}
