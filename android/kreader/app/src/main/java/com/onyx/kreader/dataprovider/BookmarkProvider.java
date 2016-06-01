package com.onyx.kreader.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;
import com.onyx.kreader.dataprovider.request.SaveBookmarkRequest;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {


    @Table(database = ReaderDatabase.class)
    static public class Bookmark extends BaseData {

        @Column
        private String quote = null;

        @Column
        private String location = null;

        @Column
        private String application = null;

        @Column
        private String position = null;

        public void setQuote(final String q) {
            quote = q;
        }

        public String getQuote() {
            return quote;
        }

        public void setLocation(final String l) {
            location = l;
        }

        public String getLocation() {
            return location;
        }

        public void setApplication(final String app) {
            application = app;
        }

        public String getApplication() {
            return application;
        }

        public void setPosition(final String p) {
            position = p;
        }

        public String getPosition() {
            return position;
        }
    }

    public static final List<Bookmark> loadBookmarks(final Context context, final String path) {
        return null;
    }

    public static void deleteBookmark(final Context context, final Bookmark bookmark) {
    }

    public static boolean hasBookmark(final Context context, final String pageName) {
        return false;
    }
}
