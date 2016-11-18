package com.onyx.android.sdk.data.cache;

import android.content.Context;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by suicheng on 2016/9/15.
 */
public class LibraryCache {

    private LinkedHashMap<String, Metadata> dataList = new LinkedHashMap<>();

    public LibraryCache() {
    }

    public void clear() {
        dataList.clear();
    }

    public void add(final Metadata metadata) {
        dataList.put(metadata.getIdString(), metadata);
    }

    public void addList(final List<Metadata> list) {
    }

    public void removeId(final String id) {
        dataList.remove(id);
    }

    public void removeId(final List<String> list) {
    }

    public List<Metadata> getValueList() {
        return new ArrayList<>(dataList.values());
    }

}
