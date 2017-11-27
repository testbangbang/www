package com.onyx.kcb.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.R;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by hehai on 17-11-17.
 */

public class LibraryViewDataModel extends Observable {
    public final ObservableList<DataModel> items = new ObservableArrayList<>();
    public final ObservableList<DataModel> visibleItems = new ObservableArrayList<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableInt count = new ObservableInt();
    public final ObservableInt libraryCount = new ObservableInt(0);
    public final ObservableList<DataModel> libraryPathList = new ObservableArrayList<>();
    private int queryLimit = 9;
    private QueryPagination queryPagination = QueryPagination.create(3, 3);
    private QueryArgs queryArgs;
    private List<DataModel> listSelected = new ArrayList<>();
    private EventBus eventBus;

    public LibraryViewDataModel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.queryArgs = new QueryArgs();
        queryArgs.limit = queryLimit;
        queryPagination.setCurrentPage(0);
    }

    public static LibraryViewDataModel create(EventBus eventBus, int rows, int cols) {
        return new LibraryViewDataModel(eventBus, rows, cols, SortBy.Name, SortOrder.Asc);
    }

    public LibraryViewDataModel(EventBus eventBus, int row, int col, SortBy sortBy, SortOrder sortOrder) {
        this(eventBus);
        queryArgs.limit = queryLimit = row * col;
        queryPagination.resize(row, col, 0);
        queryPagination.setCurrentPage(0);
        updateSortBy(sortBy, sortOrder);
    }

    public void updateSortBy(SortBy sortBy, SortOrder sortOrder) {
        queryArgs.sortBy = sortBy;
        queryArgs.order = sortOrder;
    }

    public int getOffset(int currentPage) {
        int itemsPerPage = queryPagination.itemsPerPage();
        int offset = currentPage * itemsPerPage - libraryCount.get();
        if (offset < 0) {
            if (currentPage <= libraryCount.get() / itemsPerPage) {
                offset = 0;
            } else {
                offset = itemsPerPage + offset;
            }
        }
        return offset;
    }

    public int getOffset() {
        return getOffset(queryPagination.getCurrentPage());
    }

    public QueryArgs nextPage() {
        queryArgs.offset = getOffset();
        return queryArgs;
    }

    public QueryArgs prevPage() {
        queryArgs.offset = getOffset();
        return queryArgs;
    }

    public QueryArgs pageQueryArgs(int page) {
        QueryArgs args = queryArgs.copyPart();
        args.offset = getOffset(page);
        return args;
    }

    public QueryArgs gotoPage(int page) {
        queryPagination.setCurrentPage(page);
        queryArgs.offset = getOffset(page);
        return queryArgs;
    }

    public QueryArgs firstPage() {
        return gotoPage(0);
    }

    public QueryArgs lastPage() {
        return gotoPage(queryPagination.lastPage());
    }

    public String getSdcardCid() {
        return EnvironmentUtil.getRemovableSDCardCid();
    }

    private ConditionGroup storageIdCondition() {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .or(Metadata_Table.storageId.isNull());
        String cid = getSdcardCid();
        if (StringUtils.isNotBlank(cid)) {
            conditionGroup.or(Metadata_Table.storageId.is(cid));
        }
        return conditionGroup;
    }

    public QueryArgs generateMetadataInQueryArgs(QueryArgs queryArgs) {
        return QueryBuilder.generateMetadataInQueryArgs(queryArgs);
    }

    public QueryArgs generateQueryArgs(QueryArgs queryArgs) {
        return QueryBuilder.generateQueryArgs(queryArgs);
    }

    public QueryArgs libraryQuery(int limit, int offset) {
        QueryArgs args = libraryQuery(getLibraryIdString());
        args.limit = limit;
        args.offset = offset;
        return args;
    }

    public QueryArgs libraryQuery(String libraryId) {
        QueryArgs args = buildLibraryQuery(libraryId);
        updateQueryArgs(args);
        return args;
    }

    public QueryArgs buildLibraryQuery(String libraryId) {
        QueryArgs args = new QueryArgs(queryArgs.sortBy, queryArgs.order).appendFilter(queryArgs.filter);
        args.limit = queryLimit;
        args.offset = 0;
        args.libraryUniqueId = libraryId;
        generateQueryArgs(args);
        QueryBuilder.andWith(args.conditionGroup, storageIdCondition());
        return generateMetadataInQueryArgs(args);
    }

    public QueryArgs libraryQuery() {
        return libraryQuery(queryArgs.limit, 0);
    }

    public QueryArgs getCurrentQueryArgs() {
        return this.queryArgs;
    }

    public void updateQueryArgs(QueryArgs args) {
        this.queryArgs = args;
    }

    public void getPageLibraryDataModel() {
        visibleItems.clear();
        int currentPage = queryPagination.getCurrentPage();
        int itemsPerPage = queryPagination.itemsPerPage();
        if (currentPage > libraryCount.get() / itemsPerPage) {
            for (int i = libraryCount.get(); i < min((currentPage + 1) * itemsPerPage, items.size()); i++) {
                visibleItems.add(items.get(i));
            }
        } else {
            for (int i = currentPage * itemsPerPage; i < min((currentPage + 1) * itemsPerPage, items.size()); i++) {
                visibleItems.add(items.get(i));
            }
        }
    }

    private int min(int a, int b) {
        return a < b ? a : b;
    }

    public static void libraryToDataModel(EventBus eventBus, ObservableList<DataModel> dataModels, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        for (Library library : libraryList) {
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.TYPE_LIBRARY);
            model.parentId.set(library.getParentUniqueId());
            model.id.set(library.getId());
            model.idString.set(library.getIdString());
            model.title.set(library.getName());
            model.desc.set(library.getDescription());
            model.checked.set(false);
            model.coverDefault.set(R.drawable.library_default_cover);
            dataModels.add(model);
        }
    }

    public static void metadataToDataModel(EventBus eventBus, ObservableList<DataModel> dataModels, List<Metadata> metadataList, List<DataModel> selectedList, Map<String, CloseableReference<Bitmap>> thumbnailMap) {
        if (CollectionUtils.isNullOrEmpty(metadataList)) {
            return;
        }
        for (Metadata metadata : metadataList) {
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.TYPE_METADATA);
            model.idString.set(metadata.getIdString());
            model.title.set(metadata.getName());
            model.desc.set(metadata.getDescription());
            model.absolutePath.set(metadata.getNativeAbsolutePath());
            model.checked.set(isSelected(selectedList, metadata));
            CloseableReference<Bitmap> bitmap = thumbnailMap.get(metadata.getAssociationId());
            if (bitmap != null) {
                model.coverBitMap.set(bitmap.get());
            } else {
                model.coverDefault.set(R.drawable.book_default_cover);
            }

            dataModels.add(model);
        }
    }

    private static boolean isSelected(List<DataModel> selectedList, Metadata metadata) {
        for (DataModel dataModel : selectedList) {
            if (dataModel.idString.get().equals(metadata.getIdString())) {
                return true;
            }
        }
        return false;
    }

    public List<DataModel> getListSelected() {
        return listSelected;
    }

    public void clearItemSelectedList() {
        getListSelected().clear();
    }

    public void addItemSelected(DataModel itemModel, boolean clearBeforeAdd) {
        if (clearBeforeAdd) {
            clearItemSelectedList();
        }
        getListSelected().add(itemModel);
    }

    public void removeFromSelected(DataModel itemModel) {
        Iterator<DataModel> iterator = getListSelected().iterator();
        while (iterator.hasNext()) {
            DataModel next = iterator.next();
            if (next.id.equals(itemModel.id)) {
                iterator.remove();
            }
        }
    }

    public QueryPagination getQueryPagination() {
        return queryPagination;
    }

    public String getLibraryIdString() {
        if (CollectionUtils.isNullOrEmpty(libraryPathList)) {
            return null;
        }
        return libraryPathList.get(libraryPathList.size() - 1).idString.get();
    }
}
