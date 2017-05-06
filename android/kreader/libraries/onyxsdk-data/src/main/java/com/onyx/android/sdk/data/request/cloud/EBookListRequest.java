package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;


/**
 * Created by suicheng on 2017/4/30.
 */
public class EBookListRequest extends BaseCloudRequest {

    private boolean saveToLocal = true;
    private QueryResult<Metadata> queryResult;
    private QueryArgs queryArgs;

    public EBookListRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public EBookListRequest(QueryArgs queryArgs, boolean saveToLocal) {
        this(queryArgs);
        this.saveToLocal = saveToLocal;
    }

    public QueryResult<Metadata> getProductResult() {
        return queryResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        DataProviderBase dataProvider = DataProviderManager.getCloudDataProvider(parent.getCloudConf());
        queryResult = dataProvider.findMetadataResultByQueryArgs(getContext(), queryArgs);
        if (saveToLocal) {
            for (Metadata metadata : queryResult.list) {
                metadata.save();
            }
        }
    }
}
