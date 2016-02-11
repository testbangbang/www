package com.onyx.kreader.api;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderNavigator {

    /**
     * Retrieve the default start position.
     * @return
     */
    public ReaderPagePosition getInitPosition();

    /**
     * Get position from page number. Page position can be retrieved by both index and name.
     * @param pageNumber The 0 based page number.
     * @return
     */
    public ReaderPagePosition getPositionByPageNumber(int pageNumber);

    /**
     * Create position from persistent string.
     * @param string The persistent string.
     * @return
     */
    public ReaderPagePosition createPositionFromString(final String string);

    /**
     * Return total page number.
     * @return 1 based total page number. return -1 if not available yet.
     */
    public int getTotalPage();

    /**
     * Navigate to next screen.
     */
    public ReaderPagePosition nextScreen(final ReaderPagePosition position);

    /**
     * Navigate to previous screen.
     */
    public ReaderPagePosition prevScreen(final ReaderPagePosition position);

    /**
     * Navigate to next page.
     * @return
     */
    public ReaderPagePosition nextPage(final ReaderPagePosition position);

    /**
     * Navigate to previous page.
     * @return
     */
    public ReaderPagePosition prevPage(final ReaderPagePosition position);

    /**
     * Navigate to first page.
     * @return
     */
    public ReaderPagePosition firstPage();

    /**
     * Navigate to last page.
     * @return
     */
    public ReaderPagePosition lastPage();

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final ReaderPagePosition position);

    /**
     * Retrieve links of specified page.
     * @return link list.
     */
    public List<ReaderLink> getLinks(final ReaderPagePosition position);


}
