package com.onyx.android.sdk.reader.api;

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

    /**
     * Besides search result, we also want to fetch context text around the result,
     * this method specifies the length of context text should be
     * @return
     */
    public int contextLength();
}
