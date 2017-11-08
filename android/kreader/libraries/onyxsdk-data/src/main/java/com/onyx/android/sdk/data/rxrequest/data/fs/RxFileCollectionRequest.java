package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/3/28.
 */
public class RxFileCollectionRequest extends RxBaseFSRequest {

    private List<String> dirList;
    private Set<String> extensionFilters;
    private List<String> resultFileList = new ArrayList<>();

    public RxFileCollectionRequest(DataManager dataManager,List<String> dirList, Set<String> extensionFilters) {
        super(dataManager);
        this.dirList = dirList;
        this.extensionFilters = extensionFilters;
    }

    public List<String> getResultFileList() {
        return resultFileList;
    }

    @Override
    public RxFileCollectionRequest call() throws Exception {
        if (CollectionUtils.isNullOrEmpty(dirList)) {
            return this;
        }
        for (String parentPath : dirList) {
            FileUtils.collectFiles(parentPath, extensionFilters, true, resultFileList);
        }
        return this;
    }
}
