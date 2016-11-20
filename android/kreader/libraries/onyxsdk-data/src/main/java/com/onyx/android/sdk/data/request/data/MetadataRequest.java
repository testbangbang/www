package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataRequest extends BaseDataRequest {
    private List<Metadata> list;
    private QueryArgs queryArgs;

    public MetadataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        final DataManagerHelper helper = dataManager.getDataManagerHelper();
        list = helper.getMetadataList(getContext(), queryArgs, true);
    }

    public final List<Metadata> getList() {
        return list;
    }

}
