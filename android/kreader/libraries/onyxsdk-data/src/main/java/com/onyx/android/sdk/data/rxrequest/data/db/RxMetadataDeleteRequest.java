package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by hehai on 17-12-13.
 */

public class RxMetadataDeleteRequest extends RxBaseDBRequest {
    private List<Metadata> list = new ArrayList<>();
    private List<DataModel> ignoreList;
    private QueryArgs queryArgs;

    public RxMetadataDeleteRequest(DataManager dm, List<DataModel> list) {
        super(dm);
        this.list.addAll(DataModelUtil.dataModelToMetadata(list));
    }

    public RxMetadataDeleteRequest(DataManager dm, QueryArgs queryArgs, List<DataModel> ignoreList) {
        super(dm);
        this.queryArgs = queryArgs;
        this.ignoreList = ignoreList;
    }

    @Override
    public RxMetadataDeleteRequest call() throws Exception {
        if (queryArgs != null) {
            list = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArgs);
            Iterator<Metadata> iterator = list.iterator();
            while (iterator.hasNext()) {
                Metadata metadata = iterator.next();
                if (isIgnore(metadata)) {
                    iterator.remove();
                }
            }
        }
        deleteBookList();
        return this;
    }

    private boolean isIgnore(Metadata metadata) {
        for (DataModel dataModel : ignoreList) {
            if (dataModel.idString.get().equals(metadata.getIdString())){
                return true;
            }
        }
        return false;
    }

    private void deleteBookList() {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Metadata metadata : list) {
            getDataProvider().removeMetadata(getAppContext(), metadata);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
