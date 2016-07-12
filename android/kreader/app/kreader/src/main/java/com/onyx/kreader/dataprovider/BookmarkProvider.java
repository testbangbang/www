package com.onyx.kreader.dataprovider;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {

    public static final Bookmark loadBookmark(final String application, final String md5, final String position) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.md5.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.position.eq(position))
                .querySingle();
    }

    public static final List<Bookmark> loadBookmarks(final String application, final String md5) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.md5.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .queryList();
    }

    public static void addBookmark(final Bookmark bookmark) {
        bookmark.save();
    }

    public static void deleteBookmark(final Bookmark bookmark) {
        bookmark.delete();
    }

}
