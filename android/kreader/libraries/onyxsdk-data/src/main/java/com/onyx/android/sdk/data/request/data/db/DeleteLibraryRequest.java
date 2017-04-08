package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/9.
 */
public class DeleteLibraryRequest extends BaseDBRequest {

    private Library library;

    public DeleteLibraryRequest(Library library) {
        this.library = library;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, library.getIdString());
        libraryList.add(library);
        DataManagerHelper.deleteAllLibrary(getContext(), dataManager, library.getParentUniqueId(), libraryList);
    }
}
