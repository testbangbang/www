package com.onyx.android.sdk.data.request.cloud;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/5/24.
 */
public class CloudContentRefreshRequest extends BaseCloudRequest {

    private QueryArgs queryArgs;
    private QueryResult<Metadata> queryResult;
    private Map<String, CloseableReference<Bitmap>> thumbnailBitmap = new HashMap<>();

    public CloudContentRefreshRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public QueryResult<Metadata> getProductResult() {
        return queryResult;
    }

    @Override
    public void execute(CloudManager cloudManager) throws Exception {
        checkQueryCloudPolicy(queryArgs);
        queryResult = DataManagerHelper.cloudMetadataFromDataProvider(getContext(),
                cloudManager.getCloudDataProvider(), queryArgs);
        if (queryResult == null || queryResult.isContentEmpty()) {
            return;
        }
        updateMetadataCache(cloudManager.getCacheManager(), queryArgs, queryResult);
        DataManagerHelper.saveCloudMetadataAndCollection(getContext(), cloudManager.getCloudDataProvider(),
                queryArgs, queryResult);
        thumbnailBitmap = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(getContext(), cloudManager,
                queryResult.list);
    }

    private void checkQueryCloudPolicy(QueryArgs queryArgs) {
        if (!FetchPolicy.isCloudOnlyPolicy(queryArgs.fetchPolicy)) {
            queryArgs.useCloudOnlyPolicy();
        }
    }

    private void updateMetadataCache(CacheManager cacheManager, QueryArgs queryArgs, QueryResult<Metadata> result) {
        List<Metadata> cacheList = Arrays.asList(new Metadata[(int) result.count]);
        cacheManager.addToMetadataCache(CacheManager.generateCloudKey(queryArgs), cacheList);
        DataManagerHelper.updateCloudCacheList(cacheList, result, queryArgs);
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailBitmap;
    }
}
