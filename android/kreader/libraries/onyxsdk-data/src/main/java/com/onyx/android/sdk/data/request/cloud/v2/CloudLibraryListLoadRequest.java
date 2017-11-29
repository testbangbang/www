package com.onyx.android.sdk.data.request.cloud.v2;

import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by suicheng on 2017/5/18.
 */
public class CloudLibraryListLoadRequest extends BaseCloudRequest {
    private static final String TAG = "CloudLibraryListLoad";
    private String parentId;
    private QueryArgs queryArgs = new QueryArgs();

    private List<Library> libraryList;

    public CloudLibraryListLoadRequest() {
    }

    public CloudLibraryListLoadRequest(String parentId) {
        this.parentId = parentId;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        queryArgs.cloudToken = parent.getToken();
        libraryList = loadAllLibrary(parent);
        saveLibraryListToLocal(parent.getCloudDataProvider(), queryArgs.fetchPolicy);
    }

    private List<Library> loadAllLibrary(CloudManager parent) {
        List<Library> list = parent.getCloudDataProvider().loadAllLibrary(parentId, queryArgs);
        return filterLibraryList(list);
    }

    private void saveLibraryListToLocal(DataProviderBase dataProvider, @FetchPolicy.Type int policy) {
        if (FetchPolicy.isDataFromMemDb(policy, NetworkUtil.isWiFiConnected(getContext()))) {
            return;
        }
        saveLibraryListToLocal(dataProvider, libraryList);
    }

    private void saveLibraryListToLocal(DataProviderBase dataProvider, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Library library : libraryList) {
            dataProvider.addLibrary(library);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private List<Library> filterLibraryList(List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return libraryList;
        }
        List<Library> filterList = new ArrayList<>();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            if (library == null || StringUtils.isNullOrEmpty(library.getIdString())) {
                Log.w(TAG, "detect the library or IdString is null");
                continue;
            }
            filterList.add(library);
        }
        return filterList;
    }

    public QueryArgs getQueryArgs() {
        return queryArgs;
    }
}
