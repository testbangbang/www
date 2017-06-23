package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;

/**
 * Created by suicheng on 2017/4/27.
 */

public class LibraryDataCacheClearRequest extends BaseDBRequest {

    private boolean clearLibrary = true;
    private boolean clearMetadata = true;

    public LibraryDataCacheClearRequest(boolean clearLibrary, boolean clearMetadata) {
        this.clearLibrary = clearLibrary;
        this.clearMetadata = clearMetadata;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (clearMetadata) {
            dataManager.getCacheManager().clearMetadataCache();
        }
        if (clearLibrary) {
            dataManager.getCacheManager().clearLibraryCache();
        }
    }
}
