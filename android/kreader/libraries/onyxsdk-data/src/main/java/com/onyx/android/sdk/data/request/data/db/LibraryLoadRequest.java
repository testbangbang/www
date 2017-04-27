package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryLoadRequest extends BaseDBRequest {

    private boolean loadMetadata = true;
    private QueryArgs queryArgs;

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

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<Library> tmpList = DataManagerHelper.loadLibraryListWithCache(getContext(), dataManager, queryArgs.libraryUniqueId);
        if (!CollectionUtils.isNullOrEmpty(tmpList)) {
            libraryList.addAll(tmpList);
        }
        if (loadMetadata) {
            totalCount = dataManager.getRemoteContentProvider().count(getContext(), queryArgs);
            List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getContext(), dataManager, queryArgs);
            if (!CollectionUtils.isNullOrEmpty(metadataList)) {
                bookList.addAll(metadataList);
            }
        }
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    public long getTotalCount() {
        return totalCount;
    }
}