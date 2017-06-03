package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadFromCloudRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadFromLocalRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountSaveToLocalRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.v1.OnyxAccountService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhuzeng on 03/06/2017.
 */

public class NeoCloudTest extends ApplicationTestCase<Application> {

    private CloudStore schoolCloudStore;
    public NeoCloudTest() {
        super(Application.class);
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // authentication with hardware info.
    public void testAuth() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final BaseAuthAccount account = AccountLoadFromCloudRequest.createAuthAccountFromHardware(getContext());
        assertNotNull(account);
        final AccountLoadFromCloudRequest accountGetRequest = new AccountLoadFromCloudRequest<>(account, EduAccount.class);
        getSchoolCloudStore().submitRequest(getContext(), accountGetRequest, new BaseCallback() {
            @OverrideMarketApplication.java
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(accountGetRequest.getToken());
                assertNotNull(accountGetRequest.getNeoAccount().getName());
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }



    public CloudStore getSchoolCloudStore() {
        if (schoolCloudStore == null) {
            CloudStore.init(getContext());
            schoolCloudStore = new CloudStore();
            schoolCloudStore.setCloudConf(getCloudConf());
        }
        return schoolCloudStore;
    }

    public CloudConf getCloudConf() {
        final String CLOUD_CONTENT_DEFAULT_HOST = "http://oa.o-in.me:9058/";
        final String CLOUD_CONTENT_DEFAULT_API = "http://oa.o-in.me:9058/api/";

        String host = CLOUD_CONTENT_DEFAULT_HOST;
        String api = CLOUD_CONTENT_DEFAULT_API;
        CloudConf cloudConf = new CloudConf(host, api, Constant.DEFAULT_CLOUD_STORAGE);
        return cloudConf;
    }
}
