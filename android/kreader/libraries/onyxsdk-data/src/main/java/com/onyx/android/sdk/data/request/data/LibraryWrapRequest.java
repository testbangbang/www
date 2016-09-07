package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.QueryArgsUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryWrapRequest extends BaseDataRequest {
    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private QueryArgs queryArgs;

    public LibraryWrapRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        bookList = getMetadataList(dataManager);
        libraryList = getLibraryList(dataManager);
    }

    private List<Metadata> getMetadataList(DataManager dataManager) throws Exception {
        List<Metadata> cacheList = dataManager.getDataCacheManager().getMetadataList(queryArgs);
        if (CollectionUtils.isNullOrEmpty(cacheList)) {
            List<Metadata> list = getMetadataFromDb(dataManager);
            if (queryArgs.filter == QueryArgs.BookFilter.ALL) {
                cacheList.addAll(list);
                return cacheList;
            }
            return list;
        }
        return cacheList;
    }

    private List<Metadata> getMetadataFromDb(DataManager dataManager) throws Exception {
        QueryArgsUtils.generateQueryArgsCondition(queryArgs);
        MetaDataRequest metaDataRequest = new MetaDataRequest(queryArgs);
        metaDataRequest.execute(dataManager);
        return metaDataRequest.getList();
    }

    private List<Library> getLibraryList(DataManager dataManager) throws Exception {
        LibraryRequest libraryRequest = new LibraryRequest(queryArgs.parentId);
        libraryRequest.execute(dataManager);
        return libraryRequest.getLibraryList();
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}
