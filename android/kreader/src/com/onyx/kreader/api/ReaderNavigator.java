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
    public String getInitPosition();

    /**
     * Get position from page number. Page position can be retrieved by both index and name.
     * @param pageNumber The 0 based page number.
     * @return
     */
    public String getPositionByPageNumber(int pageNumber);

    /**
     * Get position from page name.
     * @param pageName The page based page number.
     * @return
     */
    public String getPositionByPageName(final String pageName);

    /**
     * Return total page number.
     * @return 1 based total page number. return -1 if not available yet.
     */
    public int getTotalPage();

    /**
     * Navigate to next screen.
     */
    public String nextScreen(final String position);

    /**
     * Navigate to previous screen.
     */
    public String prevScreen(final String position);

    /**
     * Navigate to next page.
     * @return
     */
    public String nextPage(final String position);

    /**
     * Navigate to previous page.
     * @return
     */
    public String prevPage(final String position);

    /**
     * Navigate to first page.
     * @return
     */
    public String firstPage();

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage();

    /**
     * Retrieve links of specified page.
     * @return link list.
     */
    public List<ReaderLink> getLinks(final String position);


}
