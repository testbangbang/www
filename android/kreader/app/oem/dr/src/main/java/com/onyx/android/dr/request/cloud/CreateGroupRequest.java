package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.CreateGroupBean;
import com.onyx.android.dr.bean.CreateGroupResultBean;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class CreateGroupRequest extends BaseCloudRequest {
    private CreateGroupBean createGroupBeen;
    private CreateGroupResultBean createGroupResultBean;

    public CreateGroupRequest(CreateGroupBean bean) {
        this.createGroupBeen = bean;
    }

    public CreateGroupResultBean getResult() {
        return createGroupResultBean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getCreateGroupState(parent);
    }

    private void getCreateGroupState(CloudManager parent) {
        createGroupResultBean  = new CreateGroupResultBean();
        createGroupResultBean.setToken(DRApplication.getInstance().getString(R.string.school));
    }
}
