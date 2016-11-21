package com.onyx.android.sdk.data.cache;

import android.content.Context;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/15.
 */
public class LibraryCache {

    private LinkedHashMap<String, Metadata> dataList = new LinkedHashMap<>();
    private boolean isDirty;

    public LibraryCache() {
    }

    public void clear() {
        dataList.clear();
    }

    public void add(final Metadata metadata) {
        dataList.put(metadata.getIdString(), metadata);
    }

    public void addAll(final List<Metadata> list) {
        for(Metadata metadata : list) {
            dataList.put(metadata.getIdString(), metadata);
        }
    }

    public void removeId(final String id) {
        dataList.remove(id);
    }

    public void removeAll(final List<Metadata> list) {
        for(Metadata metadata : list) {
            dataList.remove(metadata.getIdString());
        }
    }

    public List<Metadata> getValueList() {
        return new ArrayList<>(dataList.values());
    }

    public final LinkedHashMap<String, Metadata> getDataList() {
        return dataList;
    }

    public int size() {
        return dataList.size();
    }

    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public List<File> diffList(final HashMap<String, Long> newMap) {
        List<File> diff = new ArrayList<File>();
        if (dataList != null && newMap != null) {
            for(Map.Entry<String, Long> entry : newMap.entrySet()) {
                final String key = entry.getKey();
                if (!dataList.containsKey(key) || dataList.get(key).getLastModified().getTime() != entry.getValue().longValue()) {
                    diff.add(new File(key));
                }
            }
        } else if (dataList == null && newMap != null) {
            for(Map.Entry<String, Long> entry : newMap.entrySet()) {
                final String key = entry.getKey();
                diff.add(new File(key));
            }
        }
        return diff;
    }

}
