package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.ExitGroupRequest;
import com.onyx.android.dr.request.cloud.RequestAllGroup;
import com.onyx.android.sdk.common.request.BaseCallback;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class ExitGroupData {

    public void requestAllGroup(RequestAllGroup req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void exitGroup(ExitGroupRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
