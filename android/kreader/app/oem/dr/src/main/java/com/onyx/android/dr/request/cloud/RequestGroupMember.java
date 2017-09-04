package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GroupMemberBean;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestGroupMember extends BaseCloudRequest {
    private List<GroupMemberBean> groupMemberList = new ArrayList<>();
    private int number = 10;

    public RequestGroupMember() {
    }

    public List<GroupMemberBean> getGroup() {
        return groupMemberList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        for (int i = 0; i < number; i++) {
            GroupMemberBean bean = new GroupMemberBean();
            bean.setMemberName(DRApplication.getInstance().getString(R.string.group));
            groupMemberList.add(bean);
        }
    }
}
