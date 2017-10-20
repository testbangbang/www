package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.DisposePendingGroupRequest;
import com.onyx.android.dr.request.cloud.RequestGetPendingGroups;
import com.onyx.android.sdk.common.request.BaseCallback;


/**
 * Created by zhouzhiming on 2017/10/19.
 */
public class ApplyForGroupData {

    public void getPendingGroup(RequestGetPendingGroups req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void disposePendingGroup(DisposePendingGroupRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
