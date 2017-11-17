package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;

import java.util.HashSet;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class RxFileSystemDiffRequest extends RxBaseFSRequest {

    private String storageId;
    private HashSet<String> newSnapshot = new HashSet<>();
    private HashSet<String> added = new HashSet<>();
    private HashSet<String> removed = new HashSet<>();

    public RxFileSystemDiffRequest(DataManager dataManager, final String s, final HashSet<String> files) {
        super(dataManager);
        storageId = s;
        newSnapshot.addAll(files);
    }

    @Override
    public RxFileSystemDiffRequest call() throws Exception {
        getDataManager().getFileSystemManager().diff(storageId, newSnapshot, added, removed);
        return this;
    }

    public HashSet<String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }
}
