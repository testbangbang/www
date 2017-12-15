package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.dataprovider.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */
public class RxLibraryGotoRequest extends RxBaseDBRequest {

    private boolean loadFromCache = false;
    private boolean loadParent = true;
    private DataModel gotoDataModel;
    private List<DataModel> parentLibraryList = new ArrayList<>();
    private List<Library> subLibraryList = new ArrayList<>();

    public RxLibraryGotoRequest(DataManager dataManager, DataModel gotoDataModel) {
        super(dataManager);
        this.gotoDataModel = gotoDataModel;
    }

    public List<DataModel> getParentLibraryList() {
        return parentLibraryList;
    }

    public List<Library> getSubLibraryList() {
        return subLibraryList;
    }

    @Override
    public RxLibraryGotoRequest call() throws Exception {
        subLibraryList = DataManagerHelper.loadLibraryListWithCache(getAppContext(), getDataManager(),
                gotoDataModel.idString.get(), loadFromCache);
        if (loadParent) {
            Library gotoLibrary = new Library();
            gotoLibrary.setIdString(gotoDataModel.idString.get());
            gotoLibrary.setParentUniqueId(gotoDataModel.parentId.get());
            List<Library> libraries = DataManagerHelper.loadParentLibraryList(getAppContext(), getDataManager(), gotoLibrary);
            DataModelUtil.libraryToDataModel(getDataProvider(), gotoDataModel.getEventBus(), parentLibraryList, libraries, R.drawable.library_default_cover);
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
