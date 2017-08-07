package com.onyx.android.eschool;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.manager.LeanCloudManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhuzeng on 23/06/2017.
 */

public class CloudTest extends ApplicationTestCase<Application> {


    private static ContentService service;
    private static CloudManager cloudManager = new CloudManager();

    public CloudTest() {
        super(Application.class);
    }

    private final ContentService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getContentService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public void testIndexService() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(Constant.CLOUD_MAIN_INDEX_SERVER_API,
                createIndexService(getContext()));
        cloudManager.submitRequest(getContext(), indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(indexServiceRequest.getResultIndexService());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        authService.installationId = LeanCloudManager.getInstallationId();
        return authService;
    }


}

