package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;

/**
 * Created by suicheng on 2017/4/27.
 */

public class RxLibraryDataCacheClearRequest extends RxBaseDBRequest {

    private boolean clearLibrary = true;
    private boolean clearMetadata = true;

    public RxLibraryDataCacheClearRequest(DataManager dataManager, boolean clearLibrary, boolean clearMetadata) {
        super(dataManager);
        this.clearLibrary = clearLibrary;
        this.clearMetadata = clearMetadata;
    }

    @Override
    public RxLibraryDataCacheClearRequest call() throws Exception {
        if (clearMetadata) {
            getDataManager().getCacheManager().clearMetadataCache();
        }
        if (clearLibrary) {
            getDataManager().getCacheManager().clearLibraryCache();
        }
        return this;
    }
}
