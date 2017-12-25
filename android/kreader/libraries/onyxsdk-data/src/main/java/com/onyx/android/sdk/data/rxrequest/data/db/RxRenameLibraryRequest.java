package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;

/**
 * Created by hehai on 17-12-22.
 */

public class RxRenameLibraryRequest extends RxBaseDBRequest {
    private String libraryId;
    private String newName;

    public RxRenameLibraryRequest(DataManager dm, String libraryId, String newName) {
        super(dm);
        this.libraryId = libraryId;
        this.newName = newName;
    }

    @Override
    public RxRenameLibraryRequest call() throws Exception {
        Library library = getDataProvider().loadLibrary(libraryId);
        library.setName(newName);
        getDataProvider().updateLibrary(library);
        return this;
    }
}
