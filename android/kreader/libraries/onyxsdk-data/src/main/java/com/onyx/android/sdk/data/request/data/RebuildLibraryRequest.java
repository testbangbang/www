package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;

/**
 * Created by suicheng on 2016/9/12.
 */
public class RebuildLibraryRequest extends BaseDataRequest {

    private Library library;
    private QueryArgs queryArgs;

    public RebuildLibraryRequest(final Library lib, final QueryArgs args) {
        library = lib;
        queryArgs = args;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.getDataManagerHelper().getLibraryHelper().rebuildLibrary(getContext(), library, queryArgs);
    }
}
