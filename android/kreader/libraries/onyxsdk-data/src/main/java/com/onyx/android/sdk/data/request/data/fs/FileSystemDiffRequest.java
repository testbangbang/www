package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemDiffRequest extends BaseFSRequest {

    private HashSet<String> newSnapshot;
    private HashSet<String> added = new HashSet<>();
    private HashSet<String> removed = new HashSet<>();

    public FileSystemDiffRequest(final HashSet<String> s) {
        newSnapshot = s;
    }

    public void execute(final DataManager dataManager) throws Exception {
        dataManager.getFileSystemManager().diff(newSnapshot, added, removed);
    }

    public HashSet<String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }
}
