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
        getCloudManager().submitRequest(context, indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                // TODO: 2017/12/5 for test
                String REQUEST_URL = "http://120.78.79.5/";
                String REQUEST_URL_API = "http://120.78.79.5/api/";
                getCloudManager().setAllCloudConf(CloudConf.create(REQUEST_URL,
                        REQUEST_URL_API, Constant.DEFAULT_CLOUD_STORAGE));
                getCloudManager().setCloudDataProvider(getCloudManager().getCloudConf());
                // TODO: 2017/12/5 for test
                String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OWVkOWIyYTA3Njk3ZDRiNGYzOGYzMzYiLCJyb2xlIjoidGVhY2hlciIsImlhdCI6MTUxMjUyODkxMiwiZXhwIjoxNTE1MTIwOTEyfQ.E2uTgqkhl0iYV-qNCo8tglDQMnSZ-C60ZvgjXdh96LE";
                ServiceFactory.addRetrofitTokenHeader(REQUEST_URL_API,
                        Constant.HEADER_AUTHORIZATION,
                        ContentService.CONTENT_AUTH_PREFIX + TEST_TOKEN);
                baseCallback.done(request, e);
            }
        });
    }
}
