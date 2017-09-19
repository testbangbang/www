package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.SearchGroupBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestGetRelatedGroup extends BaseCloudRequest {
    private final String name;
    private List<SearchGroupBean> groups = new ArrayList<>();

    public RequestGetRelatedGroup(String name) {
        this.name = name;
    }

    public List<SearchGroupBean> getGroup() {
        return groups;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<List<SearchGroupBean>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).getRelatedGroup(name));
            if (response != null) {
                groups = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
