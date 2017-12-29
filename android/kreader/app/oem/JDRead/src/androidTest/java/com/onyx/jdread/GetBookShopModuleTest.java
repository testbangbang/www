package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModule;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModuleList;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

import java.util.concurrent.CountDownLatch;

public class GetBookShopModuleTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = GetBookShopModuleTest.class.getSimpleName();

    public GetBookShopModuleTest() {
        super(JDReadApplication.class);
    }

    public void testGetBookDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        long bookID = 30310588;
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.NewBookDetail.TYPE, CloudApiContext.NewBookDetail.BOOK_SPECIAL_PRICE_TYPE);
        body.put(CloudApiContext.NewBookDetail.BOOK_ID, bookID);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookDetail rq = new RxRequestBookDetail();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookDetail>() {
            @Override
            public void onNext(RxRequestBookDetail request) {
                assertNotNull(request.getBookDetailResultBean());
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

    public void testGetTodaySpecial() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookShopModule.ID, CloudApiContext.BookShopModule.TODAY_SPECIAL_ID);
        body.put(CloudApiContext.BookShopModule.MODULE_TYPE, CloudApiContext.BookShopModule.TODAY_SPECIAL_MODULE_TYPE);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookModule rq = new RxRequestBookModule();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookModule>() {
            @Override
            public void onNext(RxRequestBookModule request) {
                assertNotNull(request.getBookModelResultBean());
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

    public void testGetModuleList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookShopModuleList.SYS_ID, "1");
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookModuleList rq = new RxRequestBookModuleList();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookModule>() {
            @Override
            public void onNext(RxRequestBookModule request) {
                assertNotNull(request.getBookModelResultBean());
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

    public void testGetCategoryList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.CategoryList.CLIENT_PLATFORM, CloudApiContext.CategoryList.CLIENT_PLATFORM_VALUE);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestCategoryList rq = new RxRequestCategoryList();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestCategoryList>() {
            @Override
            public void onNext(RxRequestCategoryList requestCategoryList) {
                assertNotNull(requestCategoryList.getCategoryListResultBean());
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