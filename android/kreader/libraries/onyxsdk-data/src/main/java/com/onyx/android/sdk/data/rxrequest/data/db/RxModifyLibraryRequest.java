package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;

/**
 * Created by suicheng on 2016/9/12.
 */
public class RxModifyLibraryRequest extends RxBaseDBRequest {
    private Library library;
    private boolean modifyCriteria = false;

    public RxModifyLibraryRequest(DataManager dataManager,Library library) {
        super(dataManager);
        this.library = library;
    }

    public RxModifyLibraryRequest(DataManager dataManager,Library library, boolean modifyCriteria) {
        super(dataManager);
        this.library = library;
        this.modifyCriteria = modifyCriteria;
    }

    @Override
    public RxModifyLibraryRequest call() throws Exception {
        if (!modifyCriteria) {
            getDataManager().getRemoteContentProvider().updateLibrary(library);
            return this;
        }
        return this;
    }
}
