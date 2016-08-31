package com.onyx.android.sdk.data.utils;

/**
 * Created by zhuzeng on 12/14/15.
 */
public class QueryUtils {
	
    static public String defaultSortBy() {
        return "createdAt";
    }

    static public String sortByName() {
        return "name";
    }

    public static boolean defaultSortOrder() {
        return true;
    }
    
}
