package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by suicheng on 2017/4/10.
 */
public class FilesAddToMetadataRequest extends BaseDataRequest {
    private String storageId;
    private Set<String> addFiles;

    public FilesAddToMetadataRequest(String storageId, Set<String> addFiles) {
        this.storageId = storageId;
        this.addFiles = addFiles;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(addFiles)) {
            return;
        }
        Iterator<String> iterator = addFiles.iterator();
        while (iterator.hasNext()) {
            String filePath = iterator.next();
            File file = new File(filePath);
            if (!file.exists()) {
                continue;
            }
            Metadata metadata = dataManager.getRemoteContentProvider().findMetadataByPath(getContext(), filePath);
            if (metadata == null || !metadata.hasValidId()) {
                createMetadataAndSave(dataManager, file);
            }
        }
    }

    private void createMetadataAndSave(DataManager dataManager, File file) {
        Metadata metadata = Metadata.createFromFile(file, false);
        if (metadata != null) {
            metadata.setStorageId(storageId);
            dataManager.getRemoteContentProvider().saveMetadata(getContext(), metadata);
        }
    }
}
