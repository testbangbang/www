package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AllGroupBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestAllGroup extends AutoNetWorkConnectionBaseCloudRequest {
    private List<AllGroupBean> groups = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestAllGroup() {
    }

    public List<AllGroupBean> getGroup() {
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
            Response<List<AllGroupBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).getAllGroups());
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
