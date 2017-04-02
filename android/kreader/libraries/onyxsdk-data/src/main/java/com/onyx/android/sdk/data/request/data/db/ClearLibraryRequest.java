package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by suicheng on 2016/9/10.
 */
public class ClearLibraryRequest extends BaseDataRequest {
    private Library library;

    public ClearLibraryRequest(Library library) {
        this.library = library;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.clearLibrary(getContext(), library);
    }
}
