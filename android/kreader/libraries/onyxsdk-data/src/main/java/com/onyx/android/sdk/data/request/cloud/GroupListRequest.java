package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Group;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class GroupListRequest extends BaseCloudRequest {

    private String sessionToken;
    private List<Group> list;

    public GroupListRequest(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public List<Group> getGroupList() {
        return list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<List<Group>> call = ServiceFactory.getGroupService(parent.getCloudConf().getApiBase())
                .getGroupList(sessionToken);
        Response<List<Group>> response = call.execute();
        if (response.isSuccessful()) {
            list = response.body();
        }
    }
}
