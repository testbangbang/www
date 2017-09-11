package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.RequestPayForOrder;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by hehai on 17-9-7.
 */

public class PayData {
    public void pay(RequestPayForOrder req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
