package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.FileSystemHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
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
                                             final Set<String> extension,
                                             final Set<String> ignore,
                                             final QueryArgs queryCriteria) {
        libraryUniqueId = lid;
        targetPathList = pathList;
        extensionFilters = extension;
        ignoreList = ignore;
        this.criteria = queryCriteria;
    }

    @Override
    public void execute(final DataManager dataManager) throws Exception {
        final List<File> list = collectFiles(dataManager);
        add(list);
        bookList = dataManager.getDataManagerHelper().getLibraryHelper().buildLibrary(getContext(), libraryUniqueId, criteria);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    private List<File> collectFiles(final DataManager dataManager) {
        HashMap<String, Long> hashMap = new HashMap<>();
        for(String path : targetPathList) {
            FileSystemHelper.collectFiles(path, null, hashMap, this, sizeLimit, ignoreList, extensionFilters);
        }
        final LibraryCache libraryCache = dataManager.getDataManagerHelper().getDataCacheManager().getLibraryCache(libraryUniqueId);
        return libraryCache.diffList(hashMap);
    }

    private void add(final List<File> list) {

    }
}
