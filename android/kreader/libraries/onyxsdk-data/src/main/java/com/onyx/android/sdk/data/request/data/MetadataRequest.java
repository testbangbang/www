package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
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
        // 1. cache is ready or not
        // 2. if cache is ready find library --> filter by queryArgs --> sort
        // 3. if cache not ready, query data provider and cache the result, filter and sort
        // into one function.
        List<Metadata> list;
        if (dataManager.getDataManagerHelper().isCacheReady()) {
            list = dataManager.getDataManagerHelper().getDataCacheManager().getAllMetadataList();
        } else {
            list = dataManager.getDataManagerHelper().getDataProvider().findMetadata(getContext(), null);
            saveToCache();
            markCacheReady();
        }
        list = dataManager.getDataManagerHelper().filter(list, queryArgs);
        dataManager.getDataManagerHelper().sortInPlace(list, queryArgs);
    }

    public final List<Metadata> getList() {
        return list;
    }

}
