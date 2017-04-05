package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class AddToLibraryRequest extends BaseDbRequest {
    private Library library;
    private List<Metadata> addList = new ArrayList<>();

    public AddToLibraryRequest(Library library, List<Metadata> addList) {
        this.library = library;
        this.addList.addAll(addList);
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {

    }
}
