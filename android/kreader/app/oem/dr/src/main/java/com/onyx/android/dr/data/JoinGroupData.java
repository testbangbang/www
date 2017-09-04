package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.JoinGroupRequest;
import com.onyx.android.dr.request.cloud.RequestGetRelatedGroup;
import com.onyx.android.sdk.common.request.BaseCallback;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class JoinGroupData {

    public void searchGroup(RequestGetRelatedGroup req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void joinGroup(JoinGroupRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
