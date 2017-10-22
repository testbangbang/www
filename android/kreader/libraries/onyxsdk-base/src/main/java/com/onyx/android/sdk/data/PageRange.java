package com.onyx.android.sdk.data;

/**
 * Created by joy on 10/19/17.
 */

public class PageRange {
    public String startPosition;
    public String endPosition;

    public PageRange(String start, String end) {
        startPosition = start;
        endPosition = end;
    }

    public static PageRange create(final String start, final String end) {
        return new PageRange(start, end);
    }
}
