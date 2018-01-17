package com.onyx.jdread;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
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
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_DETAIL_URI, String.valueOf(30224458));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
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
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        requestBean.setfType(1);
        requestBean.setModuleId(295);
        Map<String, String> queryArgs = new HashMap();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(1));
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_MODULE_URI, String.valueOf(1), String.valueOf(295));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        requestBean.setAppBaseInfo(appBaseInfo);
        RxRequestBookModule request = new RxRequestBookModule();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestBookModule>() {
            @Override
            public void onNext(RxRequestBookModule request) {
                BookModelBooksResultBean bookModelResultBean = request.getBookModelResultBean();
                assertNotNull(bookModelResultBean);
                Log.d(TAG, "onNext: message " + bookModelResultBean.message);
                Log.d(TAG, "onNext: resultCode " + bookModelResultBean.resultCode);
                Log.d(TAG, "onNext: name " + bookModelResultBean.data.items.get(0).name);
                assertTrue(bookModelResultBean.resultCode == 0);
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
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String uri = String.format(CloudApiContext.BookShopURI.SHOP_MAIN_CONFIG_URI, String.valueOf(Constants.BOOK_SHOP_DEFAULT_CID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(uri));
        requestBean.setAppBaseInfo(appBaseInfo);
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

    public void testGetCategoryList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestInfo baseRequestBean = new BaseRequestInfo();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.CATEGORY_URI));
        baseRequestBean.setAppBaseInfo(appBaseInfo);
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
