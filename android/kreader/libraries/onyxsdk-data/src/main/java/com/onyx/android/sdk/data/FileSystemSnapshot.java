package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by zhuzeng on 15/04/2017.
 */

public class FileSystemSnapshot {

    private HashSet<String> snapshot = new HashSet<>();

    public void diff(final HashSet<String> newSet, final HashSet<String> added, final HashSet<String> removed) {
        CollectionUtils.diff(snapshot, newSet, added);
        CollectionUtils.diff(newSet, snapshot, removed);
    }

    public void clear() {
        snapshot.clear();
    }

    public void addAll(final Collection<String> pathList) {
        snapshot.addAll(pathList);
    }

    public HashSet<String> getSnapshot() {
        return snapshot;
    }

}
