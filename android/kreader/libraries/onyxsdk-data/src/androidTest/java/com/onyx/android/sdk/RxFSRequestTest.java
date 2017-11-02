package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFilesDiffFromMetadataRequest;
import com.onyx.android.sdk.rx.RxCallback;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-11-1.
 */

public class RxFSRequestTest extends ApplicationTestCase<Application> {
    private static boolean dbInit = false;

    public RxFSRequestTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void init() {
        if (dbInit) {
            return;
        }
        dbInit = true;
        DataManager.init(getContext(), null);
    }

    public void testRxFilesDiffFromMetadata() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HashSet<String> fileSet = new HashSet<>();
        HashSet<String> metaSet = new HashSet<>();
        fileSet.add("a");
        fileSet.add("b");
        metaSet.add("a");
        RxFilesDiffFromMetadataRequest request = new RxFilesDiffFromMetadataRequest(new DataManager(), fileSet, metaSet);
        request.execute(new RxCallback<RxFilesDiffFromMetadataRequest>() {
            @Override
            public void onNext(RxFilesDiffFromMetadataRequest rxFilesDiffFromMetadataRequest) {
                HashSet<String> diffSet = rxFilesDiffFromMetadataRequest.getDiffSet();
                assertEquals(diffSet.size(), 1);
                assertTrue(diffSet.contains("b"));
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
