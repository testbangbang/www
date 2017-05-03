package com.onyx.android.sdk.data.request.data.db;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryLoadRequest extends BaseDBRequest {

    private boolean reCache = false;
    private boolean loadMetadata = true;
    private QueryArgs queryArgs;

    private Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();
    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private long totalCount;

    public LibraryLoadRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public LibraryLoadRequest(QueryArgs queryArgs, boolean loadMetadata) {
        this.queryArgs = queryArgs;
        this.loadMetadata = loadMetadata;
    }

    public void setReCache(boolean reCache) {
        this.reCache = reCache;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<Library> tmpList = DataManagerHelper.loadLibraryListWithCache(getContext(), dataManager,
                queryArgs.libraryUniqueId, reCache);
        if (!CollectionUtils.isNullOrEmpty(tmpList)) {
            libraryList.addAll(tmpList);
        }
        if (loadMetadata) {
            totalCount = dataManager.getRemoteContentProvider().count(getContext(), queryArgs);
            List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getContext(), dataManager,
                    queryArgs, reCache);
            if (!CollectionUtils.isNullOrEmpty(metadataList)) {
                bookList.addAll(metadataList);
                loadBitmaps(getContext(), dataManager);
            }
        }
    }

    private void loadBitmaps(Context context, DataManager dataManager) {
        thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(context, dataManager, bookList);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailMap;
    }

    public long getTotalCount() {
        return totalCount;
    }
}