package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 12 on 2017/4/5.
 */

public class BookCommentListTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = BookCommentListTest.class.getSimpleName();

    public BookCommentListTest() {
        super(JDReadApplication.class);
    }

    private final int bookId = 34009864;

    public void testBookCommentList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BookCommentsRequestBean bookCommentsRequestBean = new BookCommentsRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, 1 + "");
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_COMMENT_LIST_URI, String.valueOf(bookId));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        bookCommentsRequestBean.setAppBaseInfo(appBaseInfo);
        bookCommentsRequestBean.bookId = bookId;
        final RxRequestGetBookCommentList rq = new RxRequestGetBookCommentList();
        rq.setBookCommentsRequestBean(bookCommentsRequestBean);
        rq.execute(new RxCallback<RxRequestGetBookCommentList>() {
            @Override
            public void onNext(RxRequestGetBookCommentList request) {
                assertNotNull(request.getBookCommentsResultBean());
                assertNotNull(request.getBookCommentsResultBean().data);
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
