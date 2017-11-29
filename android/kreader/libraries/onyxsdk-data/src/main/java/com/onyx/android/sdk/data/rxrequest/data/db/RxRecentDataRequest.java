package com.onyx.android.sdk.data.rxrequest.data.db;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-11-27.
 */

public class RxRecentDataRequest extends RxBaseDBRequest {
    private EventBus eventBus;
    private List<DataModel> recentAddList = new ArrayList<>();
    private List<DataModel> recentlyReadList = new ArrayList<>();
    private Map<String, CloseableReference<Bitmap>> thumbnailMap;
    private List<Metadata> recentlyAddMetadata;
    private List<Metadata> recentlyReadMetadata;
    private int recentlyAddLimit = 9;
    private int recentlyReadLimit = 9;

    public RxRecentDataRequest(DataManager dm, EventBus eventBus) {
        super(dm);
        this.eventBus = eventBus;
    }

    public void setRecentlyAddLimit(int recentlyAddLimit) {
        this.recentlyAddLimit = recentlyAddLimit;
    }

    public void setRecentlyReadLimit(int recentlyReadLimit) {
        this.recentlyReadLimit = recentlyReadLimit;
    }

    @Override
    public RxRecentDataRequest call() throws Exception {
        QueryArgs queryArg = QueryBuilder.recentAddQuery();
        queryArg.limit = recentlyAddLimit;
        recentlyAddMetadata = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(), queryArg, false);
        queryArg = QueryBuilder.recentReadingQuery(SortBy.RecentlyRead, SortOrder.Desc);
        queryArg.limit = recentlyReadLimit;
        recentlyReadMetadata = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(), queryArg, false);
        if (!CollectionUtils.isNullOrEmpty(recentlyAddMetadata)) {
            loadBitmap(recentlyAddMetadata);
        }
        if (!CollectionUtils.isNullOrEmpty(recentlyReadMetadata)) {
            loadBitmap(recentlyReadMetadata);
        }
        DataModelUtil.metadataToDataModel(eventBus, recentAddList, recentlyAddMetadata, thumbnailMap);
        DataModelUtil.metadataToDataModel(eventBus, recentlyReadList, recentlyReadMetadata, thumbnailMap);
        return this;
    }

    private void loadBitmap(List<Metadata> list) {
        thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(getAppContext(), getDataManager(), list);
    }

    public List<DataModel> getRecentAddList() {
        return recentAddList;
    }

    public List<DataModel> getRecentlyReadList() {
        return recentlyReadList;
    }

    public List<Metadata> getRecentlyAddMetadata() {
        return recentlyAddMetadata;
    }

    public List<Metadata> getRecentlyReadMetadata() {
        return recentlyReadMetadata;
    }
}
