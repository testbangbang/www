package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */
public class RxLibraryGotoRequest extends RxBaseDBRequest {

    private boolean loadFromCache = false;
    private boolean loadParent = true;
    private Library gotoLibrary;
    private List<Library> parentLibraryList = new ArrayList<>();
    private List<Library> subLibraryList = new ArrayList<>();

    public RxLibraryGotoRequest(DataManager dataManager, Library gotoLibrary) {
        super(dataManager);
        this.gotoLibrary = gotoLibrary;
    }

    public List<Library> getParentLibraryList() {
        return parentLibraryList;
    }

    public List<Library> getSubLibraryList() {
        return subLibraryList;
    }

    @Override
    public RxLibraryGotoRequest call() throws Exception {
        subLibraryList = DataManagerHelper.loadLibraryListWithCache(getAppContext(), getDataManager(),
                gotoLibrary.getIdString(), loadFromCache);
        if (loadParent) {
            parentLibraryList.addAll(DataManagerHelper.loadParentLibraryList(getAppContext(), getDataManager(), gotoLibrary));
        }
        return this;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    public void setLoadParentLibrary(boolean loadParent) {
        this.loadParent = loadParent;
    }
}
