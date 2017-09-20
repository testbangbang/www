package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestAllGroup extends BaseCloudRequest {
    private List<CreateGroupCommonBean> groups = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestAllGroup() {
    }

    public List<CreateGroupCommonBean> getGroup() {
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
    }
}
