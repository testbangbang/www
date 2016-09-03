package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetaDataRequest extends BaseDataRequest {
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;

    public MetaDataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        list.addAll(getDataProviderBase(dataManager).findMetadata(getContext(), queryArgs));
    }

    protected DataProviderBase getDataProviderBase(DataManager dataManager) {
        return dataManager.getDataProviderManager().getDataProvider();
    }

    public final List<Metadata> getList() {
        return list;
    }

}
