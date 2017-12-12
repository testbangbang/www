package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;
import com.onyx.jdread.shop.request.cloud.RxRequestBookstoreModule;
import com.onyx.jdread.shop.request.cloud.RxRequestBookstoreModuleList;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

import java.util.concurrent.CountDownLatch;

import static com.kingsoft.iciba.sdk2.a.e;

public class GetBookstoreModuleTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = GetBookstoreModuleTest.class.getSimpleName();

    public GetBookstoreModuleTest() {
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
        body.put(CloudApiContext.BookstoreModule.ID, CloudApiContext.BookstoreModule.TODAY_SPECIAL_ID);
        body.put(CloudApiContext.BookstoreModule.MODULE_TYPE, CloudApiContext.BookstoreModule.TODAY_SPECIAL_MODULE_TYPE);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookstoreModule rq = new RxRequestBookstoreModule();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookstoreModule>() {
            @Override
            public void onNext(RxRequestBookstoreModule request) {
                assertNotNull(request.getBookstoreModuleResultBean());
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
        body.put(CloudApiContext.BookstoreModuleList.SYS_ID, "1");
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookstoreModuleList rq = new RxRequestBookstoreModuleList();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookstoreModule>() {
            @Override
            public void onNext(RxRequestBookstoreModule request) {
                assertNotNull(request.getBookstoreModuleResultBean());
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
                assertNull(e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}