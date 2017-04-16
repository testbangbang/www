package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemManager {

    private HashMap<String, FileSystemSnapshot> storageSnapshot = new HashMap<>();

    public FileSystemManager() {
    }

    public void diff(final String storageId, final HashSet<String> newSet, final HashSet<String> added, final HashSet<String> removed) {
        final FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            return;
        }
        wrapper.diff(newSet, added, removed);
    }

    public void clear(final String storageId) {
        final FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            return;
        }
        wrapper.clear();
    }

    public void addAll(final String storageId, final Collection<String> pathList) {
        final FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            return;
        }
        wrapper.addAll(pathList);
    }

}
