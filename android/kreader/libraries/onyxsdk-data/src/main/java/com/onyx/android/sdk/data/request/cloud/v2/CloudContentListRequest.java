package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by suicheng on 2017/4/30.
 */
public class CloudContentListRequest extends BaseCloudRequest {
    private static final String TAG = CloudContentListRequest.class.getSimpleName();

    private boolean loadThumbnail = true;
    private boolean saveToLocal = true;
    private QueryResult<Metadata> queryResult;
    private QueryArgs queryArgs;
    private Map<String, CloseableReference<Bitmap>> thumbnailBitmap = new HashMap<>();

    public CloudContentListRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public CloudContentListRequest(QueryArgs queryArgs, boolean saveToLocal) {
        this(queryArgs);
        this.saveToLocal = saveToLocal;
    }

    public QueryArgs getQueryArgs() {
        return queryArgs;
    }

    public QueryResult<Metadata> getProductResult() {
        return queryResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        queryResult = loadQueryResult(getContext(), parent, queryArgs);
        if (loadThumbnail && isValidQueryResult(queryResult)) {
            thumbnailBitmap = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(getContext(), parent, queryResult.list);
        }
    }

    private QueryResult<Metadata> loadQueryResult(Context context, CloudManager cloudManager, QueryArgs queryArgs) {
        QueryResult<Metadata> queryResult = new QueryResult<>();
        if (FetchPolicy.isMemPartPolicy(queryArgs.fetchPolicy)) {
            if (checkMemoryCache(cloudManager.getCacheManager(), queryArgs, queryResult)) {
                return queryResult;
            }
        }
        queryResult = loadFromDataProvider(context, cloudManager, queryArgs);
        updateMetadataCache(cloudManager.getCacheManager(), queryArgs, queryResult);
        saveToLocal(cloudManager.getCloudDataProvider(), queryArgs, queryResult);
        return buildRequireResult(queryResult, queryArgs);
    }

    private QueryResult<Metadata> buildRequireResult(QueryResult<Metadata> originResult, QueryArgs args) {
        QueryResult<Metadata> result = new QueryResult<>();
        if (!isValidQueryResult(originResult)) {
            return result;
        }
        result = originResult.copy(queryArgs.limit);
        return result;
    }

    private boolean checkMemoryCache(CacheManager cacheManager, QueryArgs queryArgs, QueryResult<Metadata> result) {
        List<Metadata> cacheList = cacheManager.getMetadataLruCache(getQueryKey(queryArgs));
        if (cacheList == null) {
            return false;
        }
        return DataManagerHelper.cloudMetadataFromCache(result, queryArgs, cacheList);
    }

    private QueryResult<Metadata> loadFromDataProvider(Context context, CloudManager cloudManager, QueryArgs queryArgs) {
        return DataManagerHelper.cloudMetadataFromDataProvider(context, cloudManager.getCloudDataProvider(), queryArgs);
    }

    private boolean isValidQueryResult(QueryResult result) {
        if (result == null || result.isContentEmpty()) {
            return false;
        }
        return true;
    }

    private void updateMetadataCache(CacheManager cacheManager, QueryArgs queryArgs, QueryResult<Metadata> result) {
        List<Metadata> cacheList = getMetadataCache(cacheManager, getQueryKey(queryArgs), (int) result.count);
        DataManagerHelper.updateCloudCacheList(cacheList, result, queryArgs);
    }

    private List<Metadata> getMetadataCache(CacheManager cacheManager, String queryKey, int contentCount) {
        List<Metadata> cacheList = cacheManager.getMetadataLruCache(queryKey);
        if (contentCount > CollectionUtils.getSize(cacheList)) {
            Metadata[] array = new Metadata[contentCount];
            cacheList = Arrays.asList(array);
            cacheManager.addToMetadataCache(queryKey, cacheList);
        }
        return cacheList;
    }

    private String getQueryKey(QueryArgs args) {
        return CacheManager.generateCloudKey(args);
    }

    private void saveToLocal(final DataProviderBase dataProvider, QueryArgs queryArgs, QueryResult<Metadata> queryResult) {
        if (!saveToLocal || !isValidQueryResult(queryResult) || !queryResult.isFetchFromCloud()) {
            return;
        }
        if (StringUtils.isNullOrEmpty(queryArgs.libraryUniqueId)) {
            Log.w(TAG, "detect libraryId is NULL");
            Log.w(TAG, "saveCollection method may delete collection associated with NULL libraryId");
        }
        final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Metadata metadata : queryResult.list) {
            dataProvider.saveMetadata(getContext(), metadata);
            saveCollection(getContext(), dataProvider, queryArgs.libraryUniqueId, metadata.getAssociationId());
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailBitmap;
    }

    public void setLoadThumbnail(boolean loadThumbnail) {
        this.loadThumbnail = loadThumbnail;
    }

    private void saveCollection(Context context, DataProviderBase dataProvider, String libraryId, String associationId) {
        DataManagerHelper.saveCloudCollection(context, dataProvider, libraryId, associationId);
    }
}
