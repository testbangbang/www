package com.onyx.kreader.dataprovider;

import android.content.Context;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {

    static public class Bookmark {

        private static final int INVALID_ID = -1;
        private long mId = INVALID_ID;
        private String mMD5 = null;
        private String mQuote = null;
        private String mLocation = null;
        private Date mUpdateTime = null;
        private String mApplication = null;
        private String mPosition = null;



    }

    public static final List<Bookmark> loadBookmarks(final Context context, final String path) {
        return null;
    }

    public static void save(final Context context, final String path, final List<Bookmark> books) {
    }

}
