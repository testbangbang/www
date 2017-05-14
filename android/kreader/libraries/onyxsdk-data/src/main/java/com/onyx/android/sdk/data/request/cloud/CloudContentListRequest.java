package com.onyx.android.sdk.data.request.cloud;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by suicheng on 2017/4/30.
 */
public class CloudContentListRequest extends BaseCloudRequest {

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

    public QueryResult<Metadata> getProductResult() {
        return queryResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        DataProviderBase dataProvider = DataProviderManager.getCloudDataProvider(parent.getCloudConf());
        queryResult = DataManagerHelper.loadCloudMetadataListWithCache(getContext(), parent, queryArgs);
        saveToLocal(dataProvider, queryResult);
        if (loadThumbnail && !CollectionUtils.isNullOrEmpty(queryResult.list)) {
            thumbnailBitmap = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(getContext(), parent, queryResult.list);
        }
    }

    private void saveToLocal(final DataProviderBase dataProvider, QueryResult<Metadata> queryResult) {
        if (queryResult == null || CollectionUtils.isNullOrEmpty(queryResult.list)) {
            return;
        }
        if (NetworkUtil.isWifiConnected(getContext()) && saveToLocal) {
            final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
            database.beginTransaction();
            for (Metadata metadata : queryResult.list) {
                dataProvider.saveMetadata(getContext(), metadata);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailBitmap;
    }

    public void setLoadThumbnail(boolean loadThumbnail) {
        this.loadThumbnail = loadThumbnail;
    }
}
