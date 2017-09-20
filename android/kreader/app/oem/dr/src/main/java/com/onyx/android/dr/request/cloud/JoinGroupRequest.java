package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.JoinGroupBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class JoinGroupRequest extends BaseCloudRequest {
    private JoinGroupBean joinGroupBean;
    private List<JoinGroupBean> groups = new ArrayList<>();

    public JoinGroupRequest(JoinGroupBean joinGroupBean) {
        this.joinGroupBean =  joinGroupBean;
    }

    public List<JoinGroupBean> getGroup() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getJoinGroupState(parent);
    }

    private void getJoinGroupState(CloudManager parent) {
        try {
            Response<List<JoinGroupBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).joinGroup(joinGroupBean));
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
