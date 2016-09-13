package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryRequest extends BaseDataRequest {
    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private QueryArgs queryArgs;
    private boolean loadMetadata = true;

    public LibraryRequest(QueryArgs queryArgs, boolean loadMetadata) {
        this.queryArgs = queryArgs;
        this.loadMetadata = loadMetadata;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        libraryList = getLibraryList(dataManager, queryArgs.parentId);
        if (loadMetadata) {
            bookList = getMetadataList(dataManager);
        }
    }

    private List<Metadata> getMetadataList(DataManager dataManager) throws Exception {
        List<Metadata> cacheList = dataManager.getDataCacheManager().getMetadataList(queryArgs);
        if (CollectionUtils.isNullOrEmpty(cacheList)) {
            List<Metadata> list = getMetadataFromDb(dataManager);
            if (queryArgs.filter == BookFilter.ALL) {
                cacheList.addAll(list);
                return cacheList;
            }
            return MetaDataUtils.verifyReadedStatus(list, queryArgs.filter);
        }
        return cacheList;
    }

    private List<Metadata> getMetadataFromDb(DataManager dataManager) throws Exception {
        return getDataProviderBase(dataManager).findMetadata(getContext(), queryArgs);
    }

    private List<Library> getLibraryList(DataManager dataManager, String parentId) throws Exception {
        return getDataProviderBase(dataManager).loadAllLibrary(parentId);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}