package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemScanRequest extends BaseFSRequest {

    private List<String> root;
    private HashSet<String> result;
    private volatile boolean flush;

    public FileSystemScanRequest(final List<String> r, boolean f) {
        root = r;
        flush = f;
    }

    public void execute(final DataManager dataManager) throws Exception {
        result = new HashSet<>();
        for(String path : root) {
            FileUtils.collectFiles(path, null, true, result);
        }
        if (isFlush()) {
            dataManager.getFileSystemManager().clear();
            dataManager.getFileSystemManager().addAll(result);
        }
    }

    public HashSet<String> getResult() {
        return result;
    }

    public boolean isFlush() {
        return flush;
    }
}
