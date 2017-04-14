package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataRequest extends BaseDBRequest {
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;
    private long count;

    public MetadataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        count = dataManager.getRemoteContentProvider().count(getContext(), queryArgs);
        list.addAll(dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), queryArgs));
    }

    public final List<Metadata> getList() {
        return list;
    }

    public long getCount() {
        return count;
    }

}
