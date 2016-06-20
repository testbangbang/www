package com.onyx.kreader.dataprovider;

import android.content.Context;

import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {

    public static final List<Bookmark> loadBookmarks(final Context context, final String docUniqueId, final String pageName) {
        return null;
    }

    public static void deleteBookmark(final Context context, final Bookmark bookmark) {
    }

    public static boolean hasBookmark(final Context context, final String docUniqueId, final String pageName) {
        return false;
    }
}
