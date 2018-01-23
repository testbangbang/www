package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.CloudMetadataDelete;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.MetadataRequest;

/**
 * Created by hehai on 17-8-7.
 */

public class MainFragmentData {

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void loadData(CloudContentListRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void getNowReading(MetadataRequest req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void deleteMetadata(Context context, CloudMetadataDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
