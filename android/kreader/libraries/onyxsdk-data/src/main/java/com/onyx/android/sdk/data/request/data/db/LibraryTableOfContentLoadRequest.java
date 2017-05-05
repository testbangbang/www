package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */

public class LibraryTableOfContentLoadRequest extends BaseDBRequest {

    private Library rootLibrary;
    private LibraryTableOfContentEntry tableOfContentEntry;

    public LibraryTableOfContentLoadRequest(Library rootLibrary) {
        this.rootLibrary = rootLibrary;
    }

    public LibraryTableOfContentEntry getLibraryTableOfContentEntry() {
        return tableOfContentEntry;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        String parentId = null;
        if (rootLibrary != null) {
            parentId = rootLibrary.getIdString();
        }
        tableOfContentEntry = new LibraryTableOfContentEntry();
        buildTableOfContentEntry(dataManager, parentId, tableOfContentEntry);
    }

    private void buildTableOfContentEntry(DataManager dataManager, String parentId, LibraryTableOfContentEntry toc) {
        List<Library> list = DataManagerHelper.loadLibraryListWithCache(getContext(), dataManager, parentId, false);
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Library library : list) {
            LibraryTableOfContentEntry child = new LibraryTableOfContentEntry();
            child.library = library;
            toc.children.add(child);
            buildTableOfContentEntry(dataManager, library.getIdString(), child);
        }
    }
}
