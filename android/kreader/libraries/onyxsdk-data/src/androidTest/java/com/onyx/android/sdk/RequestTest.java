package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by suicheng on 2017/4/8.
 */

public class RequestTest extends ApplicationTestCase<Application> {
    private static String[] IDENTIFIER;

    class TestRequest extends BaseDataRequest {

        @Override
        public void execute(DataManager dataManager) throws Exception {
            long sleepTime = TestUtils.randInt(2 * 1000, 6 * 1000);
            sleep(sleepTime);
        }
    }

    public RequestTest() {
        super(Application.class);
        IDENTIFIER = TestUtils.defaultContentTypes().toArray(new String[0]);
    }

    private final String getRandomIdentifier(int position) {
        String identifier = IDENTIFIER[position % IDENTIFIER.length];
        if (identifier.equals("zip")) {
            identifier = null;
        }
        return identifier;
    }

    public void testRequestQueueEmpty() {
        Debug.setDebug(true);

        final DataManager dataManager = new DataManager();
        int roundCount = TestUtils.randInt(15, 22);
        for (int r = 0; r < roundCount; r++) {
            final int round = r;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final int requestCount = TestUtils.randInt(60, 80);
            for (int i = 0; i < requestCount; i++) {
                final int position = i;
                TestRequest testRequest = new TestRequest() {

                    public final String getIdentifier() {
                        return getRandomIdentifier(position);
                    }

                };
                BaseCallback baseCallback = new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        assertNull(e);
                        Log.i("###done,round:" + round, "requestIndex:" + position);
                        if (atomicInteger.incrementAndGet() == requestCount) {
                            countDownLatch.countDown();
                        }
                    }
                };
                if (TestUtils.randInt(0, 10) % 2 == 0) {
                    dataManager.submitToMulti(getContext(), testRequest, baseCallback);
                } else {
                    dataManager.submit(getContext(), testRequest, baseCallback);
                }
            }
            awaitCountDownLatch(countDownLatch);
            assertTrue(dataManager.getRequestManager().isAllQueueEmpty());
            Log.e("###done,round:" + round, "isAllQueueEmpty:true");
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
