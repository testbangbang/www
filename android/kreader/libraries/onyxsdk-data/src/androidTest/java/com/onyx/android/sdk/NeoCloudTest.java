package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryListLoadRequest;
import com.onyx.android.sdk.data.utils.CloudConf;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhuzeng on 03/06/2017.
 */

public class NeoCloudTest extends ApplicationTestCase<Application> {

    private static CloudStore schoolCloudStore;
    private static List<Library> libraryList;
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
    public void testCloud1() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final BaseAuthAccount account = AccountLoadRequest.createAuthAccountFromHardware(getContext());
        assertNotNull(account);
        final AccountLoadRequest accountLoadRequest = new AccountLoadRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        getSchoolCloudStore().submitRequest(getContext(), accountLoadRequest, new BaseCallback() {
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(accountLoadRequest.getAccount());
                assertNotNull(accountLoadRequest.getAccount().getName());
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public void testCloud2() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CloudLibraryListLoadRequest libraryListLoadRequest = new CloudLibraryListLoadRequest();
        getSchoolCloudStore().submitRequest(getContext(), libraryListLoadRequest, new BaseCallback() {
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(libraryListLoadRequest.getLibraryList());
                assertTrue(libraryListLoadRequest.getLibraryList().size() > 0);
                libraryList = libraryListLoadRequest.getLibraryList();
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public void testCloud3() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Library library = libraryList.get(0);
        final LibraryViewInfo viewInfo = LibraryViewInfo.create(3, 3);
        QueryArgs args = viewInfo.libraryQuery(library.getIdString());
        args.useCloudMemDbPolicy();
        final CloudContentListRequest contentListRequest = new CloudContentListRequest(args);
        getSchoolCloudStore().submitRequest(getContext(), contentListRequest, new BaseCallback() {
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(contentListRequest.getProductResult());
                assertFalse(contentListRequest.getProductResult().isContentEmpty());
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
