package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;

import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class RxRemoveFromLibraryRequest extends RxBaseDBRequest {

    private Library library;
    private List<Metadata> removeList;

    public RxRemoveFromLibraryRequest(DataManager dataManager,Library library, List<Metadata> list) {
        super(dataManager);
        this.library = library;
        this.removeList = list;
    }

    @Override
    public RxRemoveFromLibraryRequest call() throws Exception {
        for (Metadata metadata : removeList) {
            getDataProvider().deleteMetadataCollection(getAppContext(),
                    library.getIdString(), metadata.getIdString());
        }
        return this;
    }
}
