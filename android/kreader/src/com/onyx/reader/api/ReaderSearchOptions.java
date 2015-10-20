package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderSearchOptions {

    /**
     * The pattern to search
     * @return
     */
    public String pattern();

    /**
     * Indicate the search is case sensitive or not.
     * @return
     */
    public boolean isCaseSensitive();

    /**
     * Search page by page instead of one by one.
     * @return
     */
    public boolean isPageByPage();

    /**
     * Search from beginning when reach end of document.
     * @return
     */
    public boolean isRoundWrap();
}
