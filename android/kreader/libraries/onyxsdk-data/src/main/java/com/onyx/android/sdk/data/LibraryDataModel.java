package com.onyx.android.sdk.data;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/22.
 */
public class LibraryDataModel implements Serializable {
    public List<Metadata> visibleBookList = new ArrayList<>();
    public List<Library> visibleLibraryList = new ArrayList<>();
    public Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();
    public int bookCount;
    public int libraryCount;

    public Map<String, PushNotification> notificationMap = new HashMap<>();

    public static void clearLibraryContent(LibraryDataModel libraryDataModel) {
        if (libraryDataModel == null) {
            return;
        }
        libraryDataModel.visibleLibraryList.clear();
        libraryDataModel.libraryCount = 0;
    }

    public static LibraryDataModel create(QueryResult<Metadata> result, QueryResult<Library> libraryResult,
                                          Map<String, CloseableReference<Bitmap>> map) {
        LibraryDataModel libraryDataModel = new LibraryDataModel();
        if (result != null && !CollectionUtils.isNullOrEmpty(result.list)) {
            libraryDataModel.visibleBookList = result.list;
            libraryDataModel.bookCount = (int) result.count;
        }
        if (libraryResult != null && !CollectionUtils.isNullOrEmpty(libraryResult.list)) {
            libraryDataModel.visibleLibraryList = libraryResult.list;
            libraryDataModel.libraryCount = (int) libraryResult.count;
        }
        libraryDataModel.thumbnailMap = map;
        return libraryDataModel;
    }
}
