package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/5/24.
 */
public class CloudContentRefreshRequest extends BaseCloudRequest {

    private QueryArgs queryArgs;
    private QueryResult<Library> libraryResult;
    private QueryResult<Metadata> queryResult;
    private Map<String, CloseableReference<Bitmap>> thumbnailBitmap = new HashMap<>();

    public CloudContentRefreshRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public QueryResult<Metadata> getProductResult() {
        return queryResult;
    }

    public QueryResult<Library> getLibraryResult() {
        return libraryResult;
    }

    @Override
    public void execute(CloudManager cloudManager) throws Exception {
        checkQueryCloudPolicy(queryArgs);
        libraryResult = loadLibraryQueryResult(cloudManager);
        queryResult = DataManagerHelper.cloudMetadataFromDataProvider(getContext(),
                cloudManager.getCloudDataProvider(), queryArgs);
        if (queryResult == null || queryResult.hasException()) {
            return;
        }
        deleteCollectionSetByLibraryId(getContext(), cloudManager.getCloudDataProvider(), queryArgs.libraryUniqueId);
        if (queryResult.isContentEmpty()) {
            clearMetadataCache(cloudManager.getCacheManager(), queryArgs);
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

    private QueryResult<Library> loadLibraryQueryResult(CloudManager parent) {
        QueryResult<Library> queryResult = new QueryResult<>();
        queryResult.list = DataManagerHelper.fetchLibraryLibraryList(getContext(), parent.getCloudDataProvider(),
                queryArgs);
        queryResult.count = CollectionUtils.getSize(queryResult.list);
        return queryResult;
    }

    private void deleteCollectionSetByLibraryId(Context context, DataProviderBase dataProvider, String libraryId) {
        dataProvider.deleteMetadataCollection(context, libraryId);
    }

    private void updateMetadataCache(CacheManager cacheManager, QueryArgs queryArgs, QueryResult<Metadata> result) {
        List<Metadata> cacheList = Arrays.asList(new Metadata[(int) result.count]);
        cacheManager.addToMetadataCache(CacheManager.generateCloudKey(queryArgs), cacheList);
        DataManagerHelper.updateCloudCacheList(cacheList, result, queryArgs);
    }

    private void clearMetadataCache(CacheManager cacheManager, QueryArgs queryArgs) {
        cacheManager.clearMetadataCache(CacheManager.generateCloudKey(queryArgs));
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailBitmap;
    }
}
