package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;

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
        dataManager.getDataManagerHelper().clearLibrary(getContext(), library);
    }
}
