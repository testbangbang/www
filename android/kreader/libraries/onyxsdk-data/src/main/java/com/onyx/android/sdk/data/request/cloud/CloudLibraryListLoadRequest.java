package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.CloudLibrary;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.v1.ContentService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/18.
 */
public class CloudLibraryListLoadRequest extends BaseCloudRequest {

    private List<CloudLibrary> libraryList;

    public List<CloudLibrary> getLibraryList() {
        return libraryList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<CloudLibrary>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .loadLibraryList(ContentService.CONTENT_AUTH_PREFIX + parent.getToken()));
        if (response.isSuccessful()) {
            libraryList = response.body();
            saveLibraryListToLocal(parent.getCloudDataProvider(), libraryList);
        }
    }

    private void saveLibraryListToLocal(DataProviderBase dataProvider, List<CloudLibrary> libraryList) {
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
}
