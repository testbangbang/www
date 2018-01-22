package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.UpdateCartRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.request.cloud.RxRequestAddOrDeleteCart;
import com.onyx.jdread.shop.request.cloud.RxRequestUpdateCart;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/1/19.
 */

public class ShopCartTest extends ApplicationTestCase<JDReadApplication> {
    public ShopCartTest() {
        super(JDReadApplication.class);
    }

    public void testAddOrDeleteBook() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UpdateCartRequestBean bean = new UpdateCartRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> map = new HashMap<>();
        map.put("type", "get");
        //map.put("book_list", "");
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.CART);
        baseInfo.setSign(signValue);

        String s = JSON.toJSONString(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), s);

        bean.setBody(requestBody);
        bean.setBaseInfo(baseInfo);
        final RxRequestUpdateCart rq = new RxRequestUpdateCart();
        rq.setUpdateCartRequestBean(bean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateCartBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
