package com.onyx.jdread.library.request;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;

/**
 * Created by suicheng on 2016/9/5.
 */
public class RxSearchBookRequest extends RxBaseDBRequest {

    private boolean loadFromCache = false;
    private QueryArgs queryArgs;
    private Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();

    private List<Metadata> bookList = new ArrayList<>();
    private List<DataModel> models = new ArrayList<>();
    private EventBus eventBus;
    private long totalCount;

    public RxSearchBookRequest(DataManager dataManager, QueryArgs queryArgs) {
        super(dataManager);
        this.queryArgs = queryArgs;
    }

    public RxSearchBookRequest(DataManager dataManager, QueryArgs queryArgs, EventBus eventBus) {
        super(dataManager);
        this.eventBus = eventBus;
        this.queryArgs = queryArgs;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return generateScheduler();
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    @Override
    public RxSearchBookRequest call() throws Exception {
        bookList.clear();

        List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(),
                queryArgs, loadFromCache);
        if (!CollectionUtils.isNullOrEmpty(metadataList)) {
            bookList.addAll(metadataList);
            loadBitmaps(getAppContext(), getDataManager());
        }

        models.clear();
        DataModelUtil.metadataToDataModel(eventBus, models, bookList, thumbnailMap, ThumbnailUtils.defaultThumbnailMapping());
        return this;
    }

    private void loadBitmaps(Context context, DataManager dataManager) {
        thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(context, dataManager, bookList);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailMap() {
        return thumbnailMap;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public List<DataModel> getModels() {
        return models;
    }
}