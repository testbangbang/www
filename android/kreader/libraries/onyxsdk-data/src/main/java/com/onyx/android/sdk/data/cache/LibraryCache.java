package com.onyx.android.sdk.data.cache;

import android.content.Context;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/15.
 */
public class LibraryCache {
    private DataProviderBase dataProvider;
    private List<String> idList = new ArrayList<>();

    public LibraryCache(DataProviderBase dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void clear() {
        idList.clear();
    }

    public void addId(String id) {
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    public void addId(List<String> list) {
        if (!idList.containsAll(list)) {
            idList.addAll(list);
        }
    }

    public void removeId(String id) {
        idList.remove(id);
    }

    public void removeId(List<String> list) {
        idList.removeAll(list);
    }

    public List<String> getIdList() {
        return idList;
    }

    public List<Metadata> getList(Context context, QueryArgs args) {
        List<Metadata> list = dataProvider.findMetadata(context, args);
        if (!CollectionUtils.isNullOrEmpty(list)) {
            for (Metadata metadata : list) {
                idList.add(metadata.getIdString());
            }
        }
        return list;
    }
}
