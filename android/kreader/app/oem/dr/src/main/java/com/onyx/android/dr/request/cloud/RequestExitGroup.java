package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestExitGroup extends BaseCloudRequest {
    private final DeleteGroupMemberBean bean;
    private final String id;
    private DeleteGroupMemberBean result;

    public RequestExitGroup(String id, DeleteGroupMemberBean bean) {
        this.id = id;
        this.bean = bean;
    }

    public DeleteGroupMemberBean getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<DeleteGroupMemberBean> response = executeCall(ServiceFactory.getContentService(parent.
                    getCloudConf().getApiBase()).deleteGroup(id, bean));
            if (response != null) {
                result = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
