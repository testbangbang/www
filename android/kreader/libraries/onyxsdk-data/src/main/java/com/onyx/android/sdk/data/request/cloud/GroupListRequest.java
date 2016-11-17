package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxGroup;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupListRequest extends BaseCloudRequest {

    private List<OnyxGroup> list;

    public GroupListRequest() {
    }

    public List<OnyxGroup> getGroupList() {
        return list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<List<OnyxGroup>> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .getGroupList(getAccountSessionToken());
        Response<List<OnyxGroup>> response = executeCall(call);
        if (response.isSuccessful()) {
            list = response.body();
        }
    }
}
