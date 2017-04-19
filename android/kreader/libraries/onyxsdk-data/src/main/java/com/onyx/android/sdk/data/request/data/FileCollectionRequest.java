package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/3/28.
 */
public class FileCollectionRequest extends BaseDataRequest {

    private List<String> dirList;
    private Set<String> extensionFilters;
    private List<String> resultFileList = new ArrayList<>();

    public FileCollectionRequest(List<String> dirList, Set<String> extensionFilters) {
        this.dirList = dirList;
        this.extensionFilters = extensionFilters;
    }

    public List<String> getResultFileList() {
        return resultFileList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(dirList)) {
            return;
        }
        for (String parentPath : dirList) {
            FileUtils.collectFiles(parentPath, extensionFilters, true, resultFileList);
        }
    }
}
