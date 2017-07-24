package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/20.
 */
public class CloudGroupListRequest extends BaseCloudRequest {

    private List<CloudGroup> myGroupList;

    public List<CloudGroup> getMyGroupList() {
        return myGroupList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        myGroupList = fetchFromCloud(parent);
    }

    private List<CloudGroup> fetchFromCloud(CloudManager parent) {
        List<CloudGroup> list = new ArrayList<>();
        try {
            Response<List<CloudGroup>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).getMyGroupList());
            if (response.isSuccessful()) {
                list = response.body();
                saveToLocalDatabase(list);
            }
        } catch (Exception e) {
            list = fetchFromLocal();
        }
        return list;
    }

    private List<CloudGroup> fetchFromLocal() {
        return StoreUtils.queryDataList(CloudGroup.class);
    }

    private void saveToLocalDatabase(List<CloudGroup> list) {
        //use local database
        try {
            StoreUtils.saveToLocal(list, CloudGroup.class, true);
        } catch (Exception e) {
        }
    }
}
