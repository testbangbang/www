package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemScanRequest extends BaseFSRequest {

    private String storageId;
    private List<String> root;
    private HashSet<String> result;
    private volatile boolean overwrite;

    public FileSystemScanRequest(final String s, final List<String> r, boolean overwriteSnapshot) {
        storageId = s;
        root = r;
        overwrite = overwriteSnapshot;
    }

    public void execute(final DataManager dataManager) throws Exception {
        result = new HashSet<>();
        for(String path : root) {
            FileUtils.collectFiles(path, null, true, result);
        }
        if (isOverwrite()) {
            dataManager.getFileSystemManager().clear(storageId);
            dataManager.getFileSystemManager().addAll(storageId, result);
        }
    }

    public HashSet<String> getResult() {
        return result;
    }

    public boolean isOverwrite() {
        return overwrite;
    }
}
