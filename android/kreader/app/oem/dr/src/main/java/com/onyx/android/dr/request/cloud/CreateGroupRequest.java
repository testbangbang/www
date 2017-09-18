package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupResultBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class CreateGroupRequest extends BaseCloudRequest {
    private CreateGroupResultBean createGroupBeen;
    private List<CreateGroupResultBean> list = new ArrayList<>();

    public CreateGroupRequest(CreateGroupResultBean bean) {
        this.createGroupBeen = bean;
    }

    public List<CreateGroupResultBean> getResult() {
        return list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getCreateGroupState(parent);
    }

    private void getCreateGroupState(CloudManager parent) {
        try {
            Response<List<CreateGroupResultBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).createGroup(createGroupBeen));
            if (response != null) {
                list = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
