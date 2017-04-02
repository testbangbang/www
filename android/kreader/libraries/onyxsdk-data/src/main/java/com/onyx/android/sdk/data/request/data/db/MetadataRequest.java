package com.onyx.android.sdk.data.request.data.db;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataRequest extends BaseDataRequest {
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;
    private long count;

    public MetadataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        long s = System.currentTimeMillis();
        count = dataManager.getDataProviderBase().count(getContext(), queryArgs);
        list.addAll(dataManager.getMetadataListWithLimit(getContext(), queryArgs));
        long e = System.currentTimeMillis();
        Log.e("################", "count: " + count + " offset: " + queryArgs.offset + " ts: " + (e - s));
    }

    public final List<Metadata> getList() {
        return list;
    }

    public long getCount() {
        return count;
    }

}
