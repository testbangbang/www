package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class MoveToLibraryRequest extends BaseDataRequest {

    private Library previousLibrary;
    private Library newLibrary;
    private List<Metadata> list = new ArrayList<>();

    public MoveToLibraryRequest(final Library prev, final Library library, List<Metadata> l) {
        previousLibrary = prev;
        newLibrary = library;
        list.addAll(l);
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.getDataManagerHelper().getLibraryHelper().moveToLibrary(getContext(), previousLibrary, newLibrary, list);
    }
}
