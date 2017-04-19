package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by suicheng on 2017/4/10.
 */

public class FilesRemoveFromMetadataRequest extends BaseDataRequest {
    private Set<String> removeFiles;

    public FilesRemoveFromMetadataRequest(Set<String> removeFiles) {
        this.removeFiles = removeFiles;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(removeFiles)) {
            return;
        }
        Iterator<String> iterator = removeFiles.iterator();
        while (iterator.hasNext()) {
            String filePath = iterator.next();
            Metadata metadata = dataManager.getRemoteContentProvider().findMetadataByHashTag(getContext(), filePath, null);
            if (metadata == null) {
                continue;
            }
            dataManager.getRemoteContentProvider().removeMetadata(getContext(), metadata);
            dataManager.getRemoteContentProvider().deleteMetadataCollection(getContext(), metadata.getIdString());
        }
    }
}
