package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GroupInfoBean;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class RequestGetRelatedGroup extends BaseCloudRequest {
    private final String name;
    private List<GroupInfoBean> groups = new ArrayList<>();
    private int number = 10;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestGetRelatedGroup(String name) {
        this.name = name;
    }

    public List<GroupInfoBean> getGroup() {
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
        for (int i = 0; i < number; i++) {
            GroupInfoBean bean = new GroupInfoBean();
            bean.setGroupName(DRApplication.getInstance().getString(R.string.group));
            bean.setGroupOwnerName(DRApplication.getInstance().getString(R.string.group));
            groups.add(bean);
        }
        if (groups != null && groups.size() > 0) {
            listCheck.clear();
            for (int i = 0; i < groups.size(); i++) {
                listCheck.add(false);
            }
        }
    }
}
