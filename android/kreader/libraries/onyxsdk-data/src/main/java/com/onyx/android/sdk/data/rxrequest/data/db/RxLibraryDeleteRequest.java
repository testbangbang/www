package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/9.
 */
public class RxLibraryDeleteRequest extends RxBaseDBRequest {

    private Library library;

    public RxLibraryDeleteRequest(DataManager dataManager,Library library) {
        super(dataManager);
        this.library = library;
    }

    @Override
    public RxLibraryDeleteRequest call() throws Exception {
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(getDataManager(), libraryList, library.getIdString());
        libraryList.add(library);
        DataManagerHelper.deleteAllLibrary(getAppContext(), getDataManager(), library.getParentUniqueId(), libraryList);
        return this;
    }
}
