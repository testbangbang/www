package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by suicheng on 2017/4/17.
 */

public class FilesDiffFromMetadataRequest extends BaseDBRequest {

    private HashSet<String> fileSet;
    private HashSet<String> metaSet;
    private HashSet<String> diffSet = new HashSet<>();

    public FilesDiffFromMetadataRequest(HashSet<String> fileSet, HashSet<String> metaSet) {
        this.fileSet = fileSet;
        this.metaSet = metaSet;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        CollectionUtils.diff(metaSet, fileSet, diffSet);
    }

    public HashSet<String> getDiffSet() {
        return diffSet;
    }
}
