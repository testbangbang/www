package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BookCartDetailBean;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CartDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.request.cloud.RxRequestCartDetail;
import com.onyx.jdread.shop.request.cloud.RxRequestUpdateCart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, Object> map = new HashMap<>();
        //1.get
        map.put("type", "get");

        //2.add
        /*map.put("type", "update");
        List<Integer> list = new ArrayList<>();
        list.add(30161081);//30224503  30295822
        map.put("book_list", list);*/

        //3.delete
        /*map.put("type", "delete");
        List<Integer> list = new ArrayList<>();
        list.add(30161081);
        map.put("book_list", list);*/
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.CART);
        baseInfo.setSign(signValue);

        String s = JSON.toJSONString(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), s);

        bean.setBody(requestBody);
        bean.setBaseInfo(baseInfo);
        final RxRequestUpdateCart rq = new RxRequestUpdateCart();
        rq.setBaseShopRequestBean(bean);
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

    public void testCartDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseShopRequestBean bean = new BaseShopRequestBean();
        List<BookCartDetailBean> list = new ArrayList<>();
        BookCartDetailBean detailBean = new BookCartDetailBean();
        detailBean.ebook_id = "30300110";
        list.add(detailBean);
        String s = JSON.toJSONString(list);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), s);

        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.CART_DETAIL);
        baseInfo.setSign(signValue);

        bean.setBaseInfo(baseInfo);
        bean.setBody(requestBody);
        final RxRequestCartDetail rq = new RxRequestCartDetail();
        rq.setRequestBean(bean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                CartDetailResultBean.DataBean resultBean = rq.getResultBean();
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
