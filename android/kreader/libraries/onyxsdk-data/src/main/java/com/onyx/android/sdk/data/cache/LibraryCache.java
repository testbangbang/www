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
    private List<String> md5List = new ArrayList<>();

    public LibraryCache(DataProviderBase dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void clear() {
        md5List.clear();
    }

    public void addId(String id) {
        if (!md5List.contains(id)) {
            md5List.add(id);
        }
    }

    public void addId(List<String> list) {
        if (!md5List.containsAll(list)) {
            md5List.addAll(list);
        }
    }

    public void removeId(String id) {
        md5List.remove(id);
    }

    public void removeId(List<String> list) {
        md5List.removeAll(list);
    }

    public List<String> getIdList() {
        return md5List;
    }

    public List<Metadata> getList(Context context, QueryArgs args) {
        List<Metadata> list = dataProvider.findMetadataByQueryArgs(context, args);
        if (!CollectionUtils.isNullOrEmpty(list)) {
            for (Metadata metadata : list) {
                md5List.add(metadata.getIdString());
            }
        }
        return list;
    }
}
