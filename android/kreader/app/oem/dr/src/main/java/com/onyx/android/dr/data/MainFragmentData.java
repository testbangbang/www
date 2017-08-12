package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

/**
 * Created by hehai on 17-8-7.
 */

public class MainFragmentData {

    public void loadData(CloudContentListRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
