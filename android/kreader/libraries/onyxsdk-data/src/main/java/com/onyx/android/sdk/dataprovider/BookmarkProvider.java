package com.onyx.android.sdk.dataprovider;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {

    public static final Bookmark loadBookmark(final String application, final String md5, final String position) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.uniqueId.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.position.eq(position))
                .querySingle();
    }

    public static final List<Bookmark> loadBookmarks(final String application, final String md5) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.uniqueId.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .orderBy(Bookmark_Table.pageNumber, true)
                .queryList();
    }

    public static void addBookmark(final Bookmark bookmark) {
        bookmark.save();
    }

    public static void deleteBookmark(final Bookmark bookmark) {
        bookmark.delete();
    }

}
