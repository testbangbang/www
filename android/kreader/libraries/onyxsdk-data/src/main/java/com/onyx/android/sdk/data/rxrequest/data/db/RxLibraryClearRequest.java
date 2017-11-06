package com.onyx.android.sdk.data.rxrequest.data.db;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.utils.Benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/10.
 */
public class RxLibraryClearRequest extends RxBaseDBRequest {
    private Library library;

    public RxLibraryClearRequest(DataManager dataManager,Library library) {
        super(dataManager);
        this.library = library;
    }

    @Override
    public RxLibraryClearRequest call() throws Exception {
        Benchmark benchmark = new Benchmark();
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(getDataManager(), libraryList, library.getIdString());
        DataManagerHelper.deleteAllLibrary(getAppContext(), getDataManager(), library.getParentUniqueId(), libraryList);
        DataManagerHelper.deleteMetadataCollection(getAppContext(), getDataManager(), library.getIdString());
        Log.w(TAG, "build duration:" + benchmark.duration() + "ms");
        return this;
    }
}
