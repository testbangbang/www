package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.DeleteGroupMemberRequest;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.dr.request.cloud.SearchGroupMemberRequest;
import com.onyx.android.dr.request.cloud.ShareBookReportRequest;
import com.onyx.android.dr.request.cloud.ShareInformalEssayRequest;
import com.onyx.android.dr.request.cloud.ShareReadingRateRequest;
import com.onyx.android.sdk.common.request.BaseCallback;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class GroupMemberData {

    public void requestGroupMember(RequestGroupMember req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void requestSearchGroupMember(SearchGroupMemberRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void requestDeleteGroupMember(DeleteGroupMemberRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void shareImpression(ShareBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void shareInformalEssay(ShareInformalEssayRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void shareReadingRate(ShareReadingRateRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }
}
