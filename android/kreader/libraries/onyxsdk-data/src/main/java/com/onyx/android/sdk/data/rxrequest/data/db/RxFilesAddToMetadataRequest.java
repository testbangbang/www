package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by suicheng on 2017/4/10.
 */
public class RxFilesAddToMetadataRequest extends RxBaseDBRequest {
    private String storageId;
    private Set<String> addFiles;

    public RxFilesAddToMetadataRequest(DataManager dataManager, String storageId, Set<String> addFiles) {
        super(dataManager);
        this.storageId = storageId;
        this.addFiles = addFiles;
    }

    @Override
    public RxFilesAddToMetadataRequest call() throws Exception {
        if (CollectionUtils.isNullOrEmpty(addFiles)) {
            return this;
        }
        Iterator<String> iterator = addFiles.iterator();
        while (iterator.hasNext()) {
            String filePath = iterator.next();
            File file = new File(filePath);
            if (!file.exists()) {
                continue;
            }
            Metadata metadata = getDataManager().getRemoteContentProvider().findMetadataByPath(getAppContext(), filePath);
            if (metadata == null || !metadata.hasValidId()) {
                createMetadataAndSave(getDataManager(), file);
            }
        }
        return this;
    }

    private void createMetadataAndSave(DataManager dataManager, File file) {
        Metadata metadata = Metadata.createFromFile(file, false);
        if (metadata != null) {
            metadata.setStorageId(storageId);
            dataManager.getRemoteContentProvider().saveMetadata(getAppContext(), metadata);
        }
    }
}
