package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15. Return all category currently supported.
 */
public class ContainerListRequest extends BaseCloudRequest {
    private List<Category> containerList = new ArrayList<>();

    public ContainerListRequest() {
    }

    public final List<Category> getProductResult() {
        return containerList;
    }

    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else {
            fetchFromLocalCache(parent);
        }
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        OnyxBookStoreService service = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase());
        Response<List<Category>> response = executeCall(service.bookContainerList());
        if (response.isSuccessful()) {
            containerList = response.body();
            if (isSaveToLocal()) {
                saveToLocal(containerList);
            }
        }
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        containerList = SQLite.select().from(Category.class).queryList();
    }

    private void saveToLocal(final List<Category> result) {
        StoreUtils.saveToLocalFast(result, Category.class, true);
    }
}
