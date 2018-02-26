package com.onyx.jdread;

import android.content.Context;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.reader.request.PreloadReaderPluginRequest;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by john on 18/2/2018.
 */

public class PreloadTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = BookCommentListTest.class.getSimpleName();

    public PreloadTest() {
        super(JDReadApplication.class);
    }

    public void testPreload() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PreloadReaderPluginRequest.setAppContext(getContext());
        PreloadReaderPluginRequest request = new PreloadReaderPluginRequest();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
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
