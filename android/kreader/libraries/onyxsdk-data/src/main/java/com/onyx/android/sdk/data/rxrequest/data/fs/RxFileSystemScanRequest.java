package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.fs.BaseFSRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class RxFileSystemScanRequest extends RxBaseFSRequest {

    private String storageId;
    private List<String> root;
    private HashSet<String> result;
    private volatile boolean overwrite;

    private Set<String> extensionFilterSet;

    public RxFileSystemScanRequest(DataManager dataManager,final String s, final List<String> r, boolean overwriteSnapshot) {
        super(dataManager);
        storageId = s;
        root = r;
        overwrite = overwriteSnapshot;
    }

    public RxFileSystemScanRequest call() throws Exception {
        result = new HashSet<>();
        for(String path : root) {
            FileUtils.collectFiles(path, extensionFilterSet, true, result);
        }
        if (isOverwrite()) {
            getDataManager().getFileSystemManager().clear(storageId);
            getDataManager().getFileSystemManager().addAll(storageId, result, isOverwrite());
        }
        return this;
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
