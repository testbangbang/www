package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.request.local.RequestLocalBooks;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-6-26.
 */

public class GetLocalBooksTest extends ApplicationTestCase<DRApplication> {
    public GetLocalBooksTest() {
        super(DRApplication.class);
    }

    public void testGetLocalBook() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final RequestLocalBooks requestLocalBooks = new RequestLocalBooks();
        new DataManager().submit(DRApplication.getInstance(), requestLocalBooks, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                assertNotNull(requestLocalBooks.getBooks());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
