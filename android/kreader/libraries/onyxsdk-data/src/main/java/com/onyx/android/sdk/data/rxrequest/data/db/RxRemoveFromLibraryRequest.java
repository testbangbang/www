package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;

import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class RxRemoveFromLibraryRequest extends RxBaseDBRequest {

    private DataModel library;
    private List<DataModel> removeList;

    public RxRemoveFromLibraryRequest(DataManager dataManager,DataModel library, List<DataModel> list) {
        super(dataManager);
        this.library = library;
        this.removeList = list;
    }

    @Override
    public RxRemoveFromLibraryRequest call() throws Exception {
        for (DataModel dataModel : removeList) {
            getDataProvider().deleteMetadataCollection(getAppContext(),
                    library.idString.get(), dataModel.idString.get());
        }
        return this;
    }
}
