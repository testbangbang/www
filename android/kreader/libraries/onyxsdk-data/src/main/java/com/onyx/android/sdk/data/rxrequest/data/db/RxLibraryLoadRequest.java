package com.onyx.android.sdk.data.rxrequest.data.db;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by suicheng on 2016/9/5.
 */
public class RxLibraryLoadRequest extends RxBaseDBRequest {

    private boolean loadFromCache = true;
    private boolean loadMetadata = true;
    private QueryArgs queryArgs;
    private Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();

    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private List<DataModel> models = new ArrayList<>();
    private List<DataModel> selectedList;
    private EventBus eventBus;
    private long totalCount;

    public RxLibraryLoadRequest(DataManager dataManager, QueryArgs queryArgs) {
        super(dataManager);
        this.queryArgs = queryArgs;
    }

    public RxLibraryLoadRequest(DataManager dataManager, QueryArgs queryArgs, List<DataModel> selectedList, EventBus eventBus, boolean loadMetadata) {
        super(dataManager);
        this.eventBus = eventBus;
        this.queryArgs = queryArgs;
        this.loadMetadata = loadMetadata;
        this.selectedList = selectedList;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return generateScheduler();
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    @Override
    public RxLibraryLoadRequest call() throws Exception {
        bookList.clear();
        libraryList.clear();
        DataManagerHelper.loadLibraryList(getDataProvider(), libraryList, queryArgs);

        if (loadMetadata) {
            totalCount = getDataProvider().count(getAppContext(), queryArgs);
            List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(),
                    queryArgs, loadFromCache);
            if (!CollectionUtils.isNullOrEmpty(metadataList)) {
                bookList.addAll(metadataList);
                loadBitmaps(getAppContext(), getDataManager());
            }
        }

        models.clear();
        DataModelUtil.libraryToDataModel(eventBus, models, libraryList, R.drawable.library_default_cover);
        DataModelUtil.metadataToDataModel(eventBus, models, bookList, selectedList, thumbnailMap, ThumbnailUtils.defaultThumbnailMapping());
        return this;
    }

    private void loadBitmaps(Context context, DataManager dataManager) {
        thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(context, dataManager, bookList);
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
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