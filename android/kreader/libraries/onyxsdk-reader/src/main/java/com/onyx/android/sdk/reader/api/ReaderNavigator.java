package com.onyx.android.sdk.reader.api;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 * Page position is unique id of each page.
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
     * @param pageName The page name.
     * @return page position.
     */
    public String getPositionByPageName(final String pageName);

    public int getPageNumberByPosition(final String position);

    /**
     * Return total page number.
     * @return 1 based total page number. return -1 if not available yet.
     */
    public int getTotalPage();

    public int getScreenStartPageNumber();

    public int getScreenEndPageNumber();

    /**
     * current position in document
     *
     * @return
     */
    public String getScreenStartPosition();

    public String getScreenEndPosition();

    public int comparePosition(final String pos1, final String pos2);

    /**
     * goto position in document
     *
     * @param position
     * @return
     */
    public boolean gotoPosition(final String position);

    /**
     * goto specified page
     *
     * @param page
     * @return
     */
    public boolean gotoPage(final int page);

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
     * at document beginning
     *
     * @return
     */
    public boolean isFirstPage();

    /**
     * Navigate to first page.
     * @return
     */
    public String firstPage();

    /**
     * at document end
     *
     * @return
     */
    public boolean isLastPage();

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage();

    /**
     * Retrieve links of specified page.
     * @return link list.
     */
    public List<ReaderSelection> getLinks(final String position);

    /**
     * image list on the page
     *
     * @param position
     * @return
     */
    public List<ReaderImage> getImages(final String position);

    /**
     * rich media list on the page
     *
     * @param position
     * @return
     */
    public List<ReaderRichMedia> getRichMedias(final String position);

}
