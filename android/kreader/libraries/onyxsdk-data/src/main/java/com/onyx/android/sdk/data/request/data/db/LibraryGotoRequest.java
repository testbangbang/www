package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */
public class LibraryGotoRequest extends BaseDataRequest {

    private boolean loadFromCache = true;
    private boolean loadParent = true;
    private Library gotoLibrary;
    private List<Library> parentLibraryList = new ArrayList<>();
    private List<Library> subLibraryList = new ArrayList<>();

    public LibraryGotoRequest(Library gotoLibrary) {
        this.gotoLibrary = gotoLibrary;
    }

    public List<Library> getParentLibraryList() {
        return parentLibraryList;
    }

    public List<Library> getSubLibraryList() {
        return subLibraryList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        subLibraryList = DataManagerHelper.loadLibraryListWithCache(getContext(), dataManager,
                gotoLibrary.getIdString(), loadFromCache);
        if (loadParent) {
            parentLibraryList.addAll(DataManagerHelper.loadParentLibraryList(getContext(), dataManager, gotoLibrary));
        }
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    public void setLoadParentLibrary(boolean loadParent) {
        this.loadParent = loadParent;
    }
}
