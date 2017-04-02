package com.onyx.android.sdk.data;

import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhuzeng on 10/2/16.
 */

public class MetadataCache {

    private HashMap<String, Metadata> idMap = new HashMap<>();
    private HashMap<String, Metadata> pathMap = new HashMap<>();

    public void add(final String idString, final Metadata metadata) {
        idMap.put(idString, metadata);
        pathMap.put(metadata.getNativeAbsolutePath(), metadata);
    }

    public void remove(final Metadata metadata) {
        idMap.remove(metadata.getIdString());
        pathMap.remove(metadata.getNativeAbsolutePath());
    }

    public Metadata getById(final String idString) {
        return idMap.get(idString);
    }

    public boolean isEmpty() {
        return idMap.isEmpty();
    }

    public boolean containsId(final String idString) {
        return idMap.containsKey(idString);
    }

    public Metadata getByPath(final String path) {
        return pathMap.get(path);
    }

    public void clear() {
        idMap.clear();
        pathMap.clear();
    }

    public List<Metadata> list() {
        return new ArrayList<>(idMap.values());
    }

    public List<String> diff(final List<String> pathList) {
        List<String> result = new ArrayList<>();
        for(String path : pathList) {
            if (!pathMap.containsKey(path)) {
                result.add(path);
            }
        }
        return result;
    }
}
