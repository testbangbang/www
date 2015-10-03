package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderSearchOptions {

    public String pattern();

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
