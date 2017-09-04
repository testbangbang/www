package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.DeleteGroupMemberRequest;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.sdk.common.request.BaseCallback;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class GroupMemberData {

    public void requestGroupMember(RequestGroupMember req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void requestDeleteGroupMember(DeleteGroupMemberRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
