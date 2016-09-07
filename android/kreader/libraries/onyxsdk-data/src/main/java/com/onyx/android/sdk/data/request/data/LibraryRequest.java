package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryRequest extends BaseDataRequest {
    private List<Library> libraryList = new ArrayList<>();
    private String parentId;

    public LibraryRequest(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        libraryList = getDataProviderBase(dataManager).loadAllLibrary(parentId);
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}
