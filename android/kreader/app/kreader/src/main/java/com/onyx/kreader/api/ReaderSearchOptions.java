package com.onyx.kreader.api;

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
     * Indicate match the whole word or not.
     * @return
     */
    public boolean isMatchWholeWord();


    /**
     * From which page to search.
     * @return
     */
    public String fromPage();
}
