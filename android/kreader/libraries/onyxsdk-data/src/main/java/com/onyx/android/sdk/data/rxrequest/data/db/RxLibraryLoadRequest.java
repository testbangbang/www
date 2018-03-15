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
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;

/**
 * Created by suicheng on 2016/9/5.
 */
public class RxLibraryLoadRequest extends RxBaseDBRequest {

    private boolean loadFromCache = true;
    private boolean loadMetadata = true;
    private boolean selectAll = false;
    private QueryArgs queryArgs;
    private Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();
    private Map<String, List<DataModel>> libraryChildMap = new HashMap<>();

    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private List<DataModel> models = new ArrayList<>();
    private List<DataModel> selectedList;
    private EventBus eventBus;
    private long totalCount;
    private long libraryCount;
    private long allBookCount;

    public RxLibraryLoadRequest(DataManager dataManager, QueryArgs queryArgs) {
        super(dataManager);
        this.queryArgs = queryArgs;
    }

    public RxLibraryLoadRequest(DataManager dataManager, QueryArgs queryArgs, List<DataModel> selectedList, boolean selectAll, EventBus eventBus, boolean loadMetadata) {
        super(dataManager);
        this.eventBus = eventBus;
        this.queryArgs = queryArgs;
        this.loadMetadata = loadMetadata;
        this.selectedList = selectedList;
        this.selectAll = selectAll;
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
        libraryCount = getDataProvider().libraryCount(queryArgs.libraryUniqueId);
        totalCount = getDataProvider().count(getAppContext(), queryArgs) + getDataProvider().libraryCount(queryArgs.libraryUniqueId);
        getBooksCount();
        setQueryOffsetBounds();
        DataManagerHelper.loadLibraryList(getDataProvider(), libraryList, this.queryArgs);
        loadLibraryCover();

        if (loadMetadata && libraryList.size() < this.queryArgs.limit) {
            this.queryArgs.offset = (int) (this.queryArgs.offset - getDataProvider().libraryCount(this.queryArgs.libraryUniqueId));
            int limit = this.queryArgs.limit;
            this.queryArgs.limit = this.queryArgs.limit - libraryList.size();
            List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(),
                    this.queryArgs, loadFromCache);
            this.queryArgs.limit = limit;
            if (!CollectionUtils.isNullOrEmpty(metadataList)) {
                bookList.addAll(metadataList);
                loadBitmaps(getAppContext(), getDataManager());
            }
        }

        models.clear();
        DataModelUtil.libraryToDataModel(getDataProvider(), eventBus, models, libraryList, libraryChildMap, false, R.drawable.library_default_cover);
        DataModelUtil.metadataToDataModel(eventBus, models, bookList, thumbnailMap, ThumbnailUtils.defaultThumbnailMapping());
        if (selectAll || !CollectionUtils.isNullOrEmpty(selectedList)) {
            setChecked(models);
        }

        return this;
    }

    private void getBooksCount() {
        QueryArgs booksQueryArgs = new QueryArgs();
        List<SQLOperator> conditions = queryArgs.conditionGroup.getConditions();
        for (SQLOperator condition : conditions) {
            if (condition.toString().contains(Metadata_Table.fetchSource.getCursorKey())) {
                booksQueryArgs.conditionGroup = OperatorGroup.clause()
                        .and(Metadata_Table.fetchSource.isNot(Metadata.FetchSource.CLOUD));
            }
        }
        allBookCount = getDataProvider().count(getAppContext(), booksQueryArgs);
    }

    private void setQueryOffsetBounds() {
        if (queryArgs.offset >= totalCount) {
            long remainder = totalCount % queryArgs.limit;
            int offset = (int) (totalCount - (remainder == 0 ? queryArgs.limit : remainder));
            queryArgs.offset = offset > 0 ? offset : 0;
        }
        queryArgs.offset = queryArgs.offset < 0 ? 0 : queryArgs.offset;
    }

    private void loadLibraryCover() {
        Iterator<Library> iterator = libraryList.iterator();
        while (iterator.hasNext()) {
            Library library = iterator.next();
            QueryArgs queryArgs = QueryBuilder.allBooksQuery(this.queryArgs.sortBy, this.queryArgs.order);
            queryArgs.libraryUniqueId = library.getIdString();
            queryArgs.limit = 4;
            QueryBuilder.generateMetadataInQueryArgs(queryArgs);
            List<Metadata> metadataList = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(),
                    queryArgs, false);
            if (CollectionUtils.isNullOrEmpty(metadataList)) {
                getDataProvider().deleteLibrary(library.getIdString());
                iterator.remove();
                totalCount--;
                libraryCount--;
            } else {
                List<DataModel> childModels = new ArrayList<>();
                Map<String, CloseableReference<Bitmap>> map = DataManagerHelper.loadThumbnailBitmapsWithCache(getAppContext(), getDataManager(), metadataList);
                DataModelUtil.metadataToDataModel(eventBus, childModels, metadataList, map, ThumbnailUtils.defaultThumbnailMapping());
                libraryChildMap.put(library.getIdString(), childModels);
                thumbnailMap.putAll(map);
            }
        }
    }

    private void setChecked(List<DataModel> models) {
        for (DataModel model : models) {
            if (model.type.get() == ModelType.TYPE_METADATA) {
                model.checked.set(isChecked(model));
            }
        }
    }

    private boolean isChecked(DataModel model) {
        if (selectAll) {
            return !selectedListContains(model);
        } else {
            return selectedListContains(model);
        }
    }

    private boolean selectedListContains(DataModel model) {
        for (DataModel dataModel : selectedList) {
            if (dataModel.idString.get().equals(model.idString.get())) {
                return true;
            }
        }
        return false;
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

    public long getLibraryCount() {
        return libraryCount;
    }

    public int getMetaDataCount() {
        return (int) (getTotalCount() - getLibraryCount());
    }

    public long getAllBookCount() {
        return allBookCount;
    }
}