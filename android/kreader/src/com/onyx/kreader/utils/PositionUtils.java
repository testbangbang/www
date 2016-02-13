package com.onyx.kreader.utils;



/**
 * Created by zhuzeng on 10/5/15.
 */
public class PositionUtils {


    public static int getPageNumber(final String position) {
        return Integer.parseInt(position);
    }

    public static String fromPageNumber(int pageNumber) {
        return String.valueOf(pageNumber);
    }


}
