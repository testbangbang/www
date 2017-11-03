package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by suicheng on 2017/4/10.
 */

public class RxFilesRemoveFromMetadataRequest extends RxBaseDBRequest {
    private Set<String> removeFiles;

    public RxFilesRemoveFromMetadataRequest(DataManager dataManager, Set<String> removeFiles) {
        super(dataManager);
        this.removeFiles = removeFiles;
    }

    @Override
    public RxFilesRemoveFromMetadataRequest call() throws Exception {
        if (CollectionUtils.isNullOrEmpty(removeFiles)) {
            return this;
        }
        Iterator<String> iterator = removeFiles.iterator();
        while (iterator.hasNext()) {
            String filePath = iterator.next();
            Metadata metadata = getDataProvider().findMetadataByHashTag(getAppContext(), filePath, null);
            if (metadata == null) {
                continue;
            }
            getDataProvider().removeMetadata(getAppContext(), metadata);
            getDataProvider().deleteMetadataCollection(getAppContext(), metadata.getIdString());
        }
        return this;
    }
}
