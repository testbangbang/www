package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by suicheng on 2016/9/7.
 */
public class BuildLibraryRequest extends BaseDBRequest {
    private Library library;
    private QueryArgs criteria;
    private List<Metadata> bookList;

    public BuildLibraryRequest(Library library, QueryArgs queryCriteria) {
        this.library = library;
        this.criteria = queryCriteria;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {

    }

    public List<Metadata> getBookList() {
        return bookList;
    }
}
