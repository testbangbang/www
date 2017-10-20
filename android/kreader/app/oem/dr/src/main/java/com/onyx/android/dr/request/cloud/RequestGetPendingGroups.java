package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.PendingGroupBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestGetPendingGroups extends AutoNetWorkConnectionBaseCloudRequest {
    private List<PendingGroupBean> groups = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestGetPendingGroups() {
    }

    public List<PendingGroupBean> getGroup() {
        return groups;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<List<PendingGroupBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).getPendingGroups());
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
