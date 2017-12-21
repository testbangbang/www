package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.library.model.LibrarySelectHelper;
import com.onyx.jdread.library.model.LibrarySelectedModel;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-13.
 */

public class RxSelectedMetadataFromMultipleLibraryRequest extends RxBaseDBRequest {
    private LibrarySelectHelper selectHelper;
    private Map<String, List<Metadata>> chosenItemsMap;

    public RxSelectedMetadataFromMultipleLibraryRequest(DataManager dm, LibrarySelectHelper selectHelper) {
        super(dm);
        this.selectHelper = selectHelper;
        chosenItemsMap = new HashMap<>();
    }

    @Override
    public RxSelectedMetadataFromMultipleLibraryRequest call() throws Exception {
        Map<String, LibrarySelectedModel> childLibrarySelectedMap = new HashMap<>();
        childLibrarySelectedMap.putAll(selectHelper.getChildLibrarySelectedMap());
        childLibrarySelectedMap.put("", selectHelper.getLibrarySelectedModel(null));
        for (Map.Entry<String, LibrarySelectedModel> modelEntry : childLibrarySelectedMap.entrySet()) {
            QueryArgs queryArgs = QueryBuilder.libraryAllBookQuery(modelEntry.getKey(), SortBy.None, SortOrder.Asc);
            List<DataModel> selectedList = modelEntry.getValue().getSelectedList();
            boolean selectedAll = modelEntry.getValue().isSelectedAll();
            getSelectedFromLibrary(modelEntry.getKey(), selectedAll, queryArgs, selectedList);
        }
        return this;
    }

    private void getSelectedFromLibrary(String libraryId, boolean selectAll, QueryArgs queryArgs, List<DataModel> selectedList) {
        List<Metadata> list = new ArrayList<>();
        if (selectAll) {
            list = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArgs);
            Iterator<Metadata> iterator = list.iterator();
            while (iterator.hasNext()) {
                Metadata metadata = iterator.next();
                if (isIgnore(metadata, selectedList)) {
                    iterator.remove();
                }
            }
        } else {
            addSelectedMetadata(list, selectedList);
        }
        if (!CollectionUtils.isNullOrEmpty(list)) {
            chosenItemsMap.put(libraryId, list);
        }
    }

    private boolean isIgnore(Metadata metadata, List<DataModel> ignoreList) {
        for (DataModel dataModel : ignoreList) {
            if (dataModel.idString.get().equals(metadata.getIdString())) {
                return true;
            }
        }
        return false;
    }

    private void addSelectedMetadata(List<Metadata> list, List<DataModel> modelList) {
        if (CollectionUtils.isNullOrEmpty(modelList)) {
            return;
        }
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (DataModel dataModel : modelList) {
            Metadata metadata = getDataProvider().findMetadataByIdString(getAppContext(), dataModel.idString.get());
            list.add(metadata);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public Map<String, List<Metadata>> getChosenItemsMap() {
        return chosenItemsMap;
    }
}
