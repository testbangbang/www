package com.onyx.cloud.utils;

import java.util.List;

import com.onyx.cloud.model.BaseObject;
import com.onyx.cloud.model.DownloadLink;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

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
