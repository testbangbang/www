package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;

/**
 * Created by suicheng on 2016/9/10.
 */
public class ClearLibraryRequest extends BaseDataRequest {

    private String libraryToClear;
    private String libraryToAdopt;

    public ClearLibraryRequest(final String toClear, final String toAdopt) {
        libraryToClear = toClear;
        libraryToAdopt = toAdopt;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.getDataManagerHelper().getLibraryHelper().moveToLibrary(getContext(), libraryToClear, libraryToAdopt);
    }
}
