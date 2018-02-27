package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.util.concurrent.CountDownLatch;

public class ShoppingCartTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = ShoppingCartTest.class.getSimpleName();

    public ShoppingCartTest() {
        super(JDReadApplication.class);
    }

    public void testAddOrDeleteCart() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(new String[]{"30310588"}, Constants.CART_TYPE_ADD);
        addOrDeleteCartAction.execute(ShopDataBundle.getInstance(),new RxCallback<AddOrDeleteCartAction>() {
            @Override
            public void onNext(AddOrDeleteCartAction addOrDeleteCartAction) {
                assertNotNull(addOrDeleteCartAction.getData());
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
