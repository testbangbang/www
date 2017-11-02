package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.HashSet;

/**
 * Created by suicheng on 2017/4/17.
 */

public class RxFilesDiffFromMetadataRequest extends RxBaseFSRequest {

    private HashSet<String> fileSet;
    private HashSet<String> metaSet;
    private HashSet<String> diffSet = new HashSet<>();

    public RxFilesDiffFromMetadataRequest(DataManager dataManager, HashSet<String> fileSet, HashSet<String> metaSet) {
        super(dataManager);
        this.fileSet = fileSet;
        this.metaSet = metaSet;
    }

    @Override
    public RxFilesDiffFromMetadataRequest call() throws Exception {
        CollectionUtils.diff(metaSet, fileSet, diffSet);
        return this;
    }

    public HashSet<String> getDiffSet() {
        return diffSet;
    }
}
