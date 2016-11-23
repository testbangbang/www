package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.FileSystemHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Metadata;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2016/9/7.
 */
public class BuildLibraryFromFileSystemRequest extends BaseDataRequest {

    private List<String> targetPathList;
    private Set<String> extensionFilters;
    private Set<String> ignoreList;
    private String libraryUniqueId;
    private QueryArgs criteria;
    private List<Metadata> bookList;
    private int sizeLimit = Integer.MAX_VALUE;

    public BuildLibraryFromFileSystemRequest(final String lid,
                                             final List<String> pathList,
                                             final Set<String> ignore,
                                             final Set<String> extension,
                                             final QueryArgs queryCriteria) {
        libraryUniqueId = lid;
        targetPathList = pathList;
        extensionFilters = extension;
        ignoreList = ignore;
        criteria = queryCriteria;
    }

    @Override
    public void execute(final DataManager dataManager) throws Exception {
        final List<File> list = collectDiffFiles(dataManager);
        bookList = dataManager.getDataManagerHelper().getMetadataHelper().saveList(dataManager, getContext(), list);
        dataManager.getDataManagerHelper().getLibraryHelper().addCollections(getContext(), libraryUniqueId, bookList);
        addToLibraryCache(dataManager, bookList);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    private List<File> collectDiffFiles(final DataManager dataManager) {
        HashMap<String, Long> hashMap = new HashMap<>();
        for(String path : targetPathList) {
            FileSystemHelper.collectFiles(path, null, hashMap, this, sizeLimit, ignoreList, extensionFilters);
        }
        final LibraryCache libraryCache = dataManager.getDataManagerHelper().getDataCacheManager().getLibraryCache(libraryUniqueId);
        return libraryCache.diffList(hashMap);
    }

    private void addToLibraryCache(final DataManager dataManager, final List<Metadata> bookList) {
        dataManager.getDataManagerHelper().getDataCacheManager().addToLibrary(libraryUniqueId, bookList);
    }

}
