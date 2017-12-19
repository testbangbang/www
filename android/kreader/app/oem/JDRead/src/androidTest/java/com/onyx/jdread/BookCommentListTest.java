package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 12 on 2017/4/5.
 */

public class BookCommentListTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = BookCommentListTest.class.getSimpleName();

    public BookCommentListTest() {
        super(JDReadApplication.class);
    }

    private final String EBOOK = "ebook";
    private final int bookId = 30310588;
    private final String QUALITYEBOOK = "qualityEbook";
    private final String PAPEREBOOK = "paperBook";

    public void testBookCommentList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BookCommentsRequestBean bookCommentsRequestBean = new BookCommentsRequestBean();
        bookCommentsRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        String bookCommentsJsonBody = getBookCommentsJsonBody(EBOOK, bookId, "1");
        bookCommentsRequestBean.setBody(bookCommentsJsonBody);
        final RxRequestGetBookCommentList rq = new RxRequestGetBookCommentList();
        rq.setBookCommentsRequestBean(bookCommentsRequestBean);
        rq.execute(new RxCallback<RxRequestGetBookCommentList>() {
            @Override
            public void onNext(RxRequestGetBookCommentList request) {
                assertNotNull(request.getBookCommentsResultBean());
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

    private String getWriteCommentJsonBody(int bookid, String content, float rating, String users) {
        JSONObject json = new JSONObject();
        try {
            json.put("content", URLEncoder.encode(content, "utf-8"));
            json.put("at_user_ids", users);
            json.put("book_id", bookid);
            json.put("rating", rating);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public String getBookCommentsJsonBody(String bookType, long eBookId, String currentPage) {
        final JSONObject json = new JSONObject();
        try {
            if (bookType.equals("ebook"))
                json.put("eBookId", eBookId);
            else {
                json.put("paperBookId", eBookId);
            }
            json.put("currentPage", currentPage);
            json.put("pageSize", 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private String getCommentGetScoreBody(long bookId) {
        JSONObject jsobj = new JSONObject();
        try {
            jsobj.put("bookId", bookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsobj.toString();
    }
}
