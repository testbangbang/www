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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class BookmarkProvider {

    private static final String TAG = BookmarkProvider.class.getSimpleName();
    private RequestManager requestManager;

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
    
    public BookmarkProvider() {
        requestManager = new RequestManager();
    }

    private final Runnable generateRunnable(final BaseDataProviderRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute();
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public final List<Bookmark> loadBookmarks(final Context context, final String path) {
        return null;
    }

    public void saveInBackground(final Context context, final String path, final List<Bookmark> books, final BaseCallback callback) {
        final SaveBookmarkRequest saveBookmarkRequest = new SaveBookmarkRequest(path, books);
        requestManager.submitRequest(context, saveBookmarkRequest, generateRunnable(saveBookmarkRequest), callback);
    }

}
