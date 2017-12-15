package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.edu.homework.base.BaseAction;

/**
 * Created by ming on 2017/6/24.
 */

public class CloudIndexServiceAction extends BaseAction {

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(Constant.CLOUD_MAIN_INDEX_SERVER_API,
                IndexService.createIndexService(context));
        getCloudManager().submitRequest(context, indexServiceRequest, baseCallback);
    }
}
