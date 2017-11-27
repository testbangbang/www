package com.onyx.android.sdk.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemManager {

    private ConcurrentHashMap<String, FileSystemSnapshot> storageSnapshot = new ConcurrentHashMap<>();

    public FileSystemManager() {
    }

    public synchronized void diff(final String storageId, final HashSet<String> newSet, final HashSet<String> added, final HashSet<String> removed) {
        final FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            return;
        }
        wrapper.diff(newSet, added, removed);
    }

    public synchronized void clear(final String storageId) {
        final FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            return;
        }
        wrapper.clear();
    }

    public synchronized void addAll(final String storageId, final Collection<String> pathList, boolean createIfNotExist) {
        FileSystemSnapshot wrapper = storageSnapshot.get(storageId);
        if (wrapper == null) {
            if (!createIfNotExist) {
                return;
            }
            storageSnapshot.put(storageId, wrapper = new FileSystemSnapshot());
        }
        wrapper.addAll(pathList);
    }
}
