package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemManager {

    private HashSet<String> snapshot = new HashSet<>();

    public FileSystemManager() {
    }

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

}
