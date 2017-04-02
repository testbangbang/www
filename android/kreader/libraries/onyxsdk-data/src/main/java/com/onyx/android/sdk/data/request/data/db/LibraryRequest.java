package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class LibraryRequest extends BaseDataRequest {

    private List<Metadata> bookList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private QueryArgs queryArgs;
    private boolean loadMetadata = true;

    public LibraryRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public LibraryRequest(QueryArgs queryArgs, boolean loadMetadata) {
        this.queryArgs = queryArgs;
        this.loadMetadata = loadMetadata;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        DataManagerHelper.loadAllLibrary(libraryList, queryArgs.libraryUniqueId);
        if (loadMetadata) {
            bookList = dataManager.getLibraryMetadataList(getContext(), queryArgs);
        }
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}