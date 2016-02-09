package com.onyx.kreader.plugins.adobe;

/**
 * Created by zhuzeng on 10/14/15.
 */
public enum HitTest {
    PAGE_BEGIN,
    PAGE_END,

    /**
     * Report the location for touched point.
     */
    TOUCH,

    /**
     * try to select a word if possible.
     */
    SELECT_WORD,

}

