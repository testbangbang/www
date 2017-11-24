package com.onyx.android.sdk.reader.utils;


import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class PagePositionUtils {

    public static int getPageNumber(final String pageName) {
        return Integer.parseInt(pageName);
    }

    /**
     * increase page number with 1 for display
     * @param pageName
     * @return
     */
    public static String getPageNumberForDisplay(final String pageName) {
        int page = getPageNumber(pageName);
        return page < 0 ? "" : String.valueOf(page + 1);
    }

    public static String fromPageNumber(int pageNumber) {
        return String.valueOf(pageNumber);
    }

    public static int getPosition(final String position) {
        return Integer.parseInt(position);
    }

    public static String fromPosition(int position) {
        return String.valueOf(position);
    }

    public static boolean isValidPosition(final String position) {
        return StringUtils.isNotBlank(position);
    }

}
