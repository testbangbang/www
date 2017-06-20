package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemScanRequest extends BaseFSRequest {

    private String storageId;
    private List<String> root;
    private HashSet<String> result;
    private volatile boolean overwrite;

    private Set<String> extensionFilterSet;

    public FileSystemScanRequest(final String s, final List<String> r, boolean overwriteSnapshot) {
        storageId = s;
        root = r;
        overwrite = overwriteSnapshot;
    }

    public void execute(final DataManager dataManager) throws Exception {
        result = new HashSet<>();
        for(String path : root) {
            FileUtils.collectFiles(path, extensionFilterSet, true, result);
        }
        if (isOverwrite()) {
            dataManager.getFileSystemManager().clear(storageId);
            dataManager.getFileSystemManager().addAll(storageId, result, isOverwrite());
        }
    }

    public HashSet<String> getResult() {
        return result;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setExtensionFilterSet(Set<String> set) {
        this.extensionFilterSet = set;
    }
}
