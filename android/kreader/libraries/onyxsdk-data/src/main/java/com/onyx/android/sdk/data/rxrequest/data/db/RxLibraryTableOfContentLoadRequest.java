package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import static com.onyx.android.sdk.data.OnyxDownloadManager.getContext;

/**
 * Created by suicheng on 2017/4/29.
 */

public class RxLibraryTableOfContentLoadRequest extends RxBaseDBRequest {

    private Library rootLibrary;
    private LibraryTableOfContentEntry tableOfContentEntry;

    public RxLibraryTableOfContentLoadRequest(DataManager dataManager,Library rootLibrary) {
        super(dataManager);
        this.rootLibrary = rootLibrary;
    }

    public LibraryTableOfContentEntry getLibraryTableOfContentEntry() {
        return tableOfContentEntry;
    }

    @Override
    public RxLibraryTableOfContentLoadRequest call() throws Exception {
        String parentId = null;
        if (rootLibrary != null) {
            parentId = rootLibrary.getIdString();
        }
        tableOfContentEntry = new LibraryTableOfContentEntry();
        buildTableOfContentEntry(getDataManager(), parentId, tableOfContentEntry);
        return this;
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
