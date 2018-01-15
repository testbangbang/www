package com.onyx.jdread;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.BookModelRequestBean;
import com.onyx.jdread.shop.cloud.entity.GetBookDetailRequestBean;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModule;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModuleList;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;
import com.onyx.jdread.shop.request.cloud.RxRequestShopMainConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class GetBookShopModuleTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = GetBookShopModuleTest.class.getSimpleName();

    public GetBookShopModuleTest() {
        super(JDReadApplication.class);
    }

    public void testGetBookDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetBookDetailRequestBean baseRequestBean = new GetBookDetailRequestBean();
        JDAppBaseInfo appBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        baseRequestBean.setAppBaseInfo(appBaseInfo);
        baseRequestBean.bookId = 30224458;
        final RxRequestBookDetail rq = new RxRequestBookDetail();
        rq.setRequestBean(baseRequestBean);
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

    public void testGetModuleBooks() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BookModelRequestBean requestBean = new BookModelRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        requestBean.setAppBaseInfo(baseInfo);
        Map<String,String> body = new HashMap<>();
        body.put(CloudApiContext.SearchBook.PAGE_SIZE,Constants.BOOK_PAGE_SIZE);
        body.put(CloudApiContext.SearchBook.CURRENT_PAGE,1+"");
        requestBean.setQueryArgsMap(body);
        requestBean.setfType(1);
        requestBean.setModuleId(1);
        final RxRequestBookModule rq = new RxRequestBookModule();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback<RxRequestBookModule>() {
            @Override
            public void onNext(RxRequestBookModule request) {
                BookModelBooksResultBean bookModelResultBean = request.getBookModelResultBean();
                assertNotNull(bookModelResultBean);
                Log.d(TAG, "onNext: message " + bookModelResultBean.message);
                Log.d(TAG, "onNext: result_code " + bookModelResultBean.result_code);
                Log.d(TAG, "onNext: name " + bookModelResultBean.data.items.get(0).name);
                assertTrue(bookModelResultBean.result_code == 0);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                Log.d(TAG, "onError: " + throwable.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testShopMainConfig() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ShopMainConfigRequestBean requestBean = new ShopMainConfigRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        requestBean.setAppBaseInfo(baseInfo);

        requestBean.setCid(Constants.BOOK_SHOP_DEFAULT_CID);
        final RxRequestShopMainConfig rq = new RxRequestShopMainConfig();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback<RxRequestShopMainConfig>() {
            @Override
            public void onNext(RxRequestShopMainConfig request) {
                BookModelConfigResultBean resultBean = request.getResultBean();
                assertNotNull(resultBean.data.modules);
                assertNotNull(resultBean.data.adv);
                Log.d(TAG, "adv: " + resultBean.data.adv);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                Log.d(TAG, "onError: " + throwable.getMessage());
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
        BaseRequestInfo baseRequestBean = new BaseRequestInfo();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTid(String.valueOf(System.currentTimeMillis()));
        baseRequestBean.setAppBaseInfo(jdAppBaseInfo);
        final RxRequestCategoryList request = new RxRequestCategoryList();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestCategoryList>() {
            @Override
            public void onNext(RxRequestCategoryList requestCategoryList) {
                CategoryListResultBean categoryListResultBean = requestCategoryList.getCategoryListResultBean();
                assertNotNull(categoryListResultBean.data);
                assertTrue(categoryListResultBean.data.size() > 0);
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
