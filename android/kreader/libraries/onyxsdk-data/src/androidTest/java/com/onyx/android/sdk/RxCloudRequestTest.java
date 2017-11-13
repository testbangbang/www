package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.BaseQuery;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ProductShared;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxBookContainerListRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxBookContainerRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxBookListWithProductRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxBookRecommendedListRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxDeleteBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxDisagreeBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetBookBytesRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetBookCommentListRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetBookCoverRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetBookListRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetSingleBookRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxPostBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxProductShareListRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxSupportBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxUpdataBookCommentRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.DeleteBookCommentRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.DisagreeBookCommentRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookBytesRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCommentListRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCommentRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.GetBookCoverRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.PostBookCommentRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.SupportBookCommentRequestBean;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.UpdataBookCommentRequestBean;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxCloudRequestTest extends ApplicationTestCase<Application> {
    private ProductResult<Product> productResult;
    static OnyxAccount currentAccount;
    private Product product;
    private List<Comment> commentList;
    private Comment postComment;
    private List<Category> bookContainerList;

    public RxCloudRequestTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public static OnyxAccount getCurrentAccount() throws Exception {
        if (currentAccount == null || currentAccount.sessionToken == null) {
            currentAccount = AccountTest.testSignUpRequest();
        }
        return currentAccount;
    }

    public void testGetBookList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RxGetBookListRequest request = new RxGetBookListRequest(JSON.toJSONString(new ProductQuery()));
        request.execute(new RxCallback<RxGetBookListRequest>() {
            @Override
            public void onNext(RxGetBookListRequest request) {
                productResult = request.getResult();
                assertNotNull(productResult);
                assertTrue(productResult.count > 0);
                assertTrue(productResult.list.size() > 0);
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

    public void testSingleBook() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookList();
        final String uniqueId = productResult.list.get(0).getGuid();
        RxGetSingleBookRequest request = new RxGetSingleBookRequest(uniqueId);
        request.execute(new RxCallback<RxGetSingleBookRequest>() {
            @Override
            public void onNext(RxGetSingleBookRequest request) {
                product = request.getResult();
                assertNotNull(product);
                assertTrue(product.getGuid().equals(uniqueId));
                assertNotNull(product.formats);
                assertTrue(product.formats.size() > 0);
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

    public void testGetBookCover() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookList();
        final String uniqueId = productResult.list.get(0).getGuid();
        GetBookCoverRequestBean requestBean = new GetBookCoverRequestBean();
        requestBean.uniqueId = uniqueId;
        requestBean.type = Constant.COVER_TYPE_NORMAL;
        RxGetBookCoverRequest request = new RxGetBookCoverRequest(requestBean);
        request.execute(new RxCallback<RxGetBookCoverRequest>() {
            @Override
            public void onNext(RxGetBookCoverRequest request) {
                ResponseBody body = request.getResult();
                try {
                    assertTrue(body.bytes().length > 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void testGetBookBytes() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookList();
        testSingleBook();
        final String uniqueId = productResult.list.get(0).getGuid();
        String type = product.formats.toArray(new String[]{})[0];
        GetBookBytesRequestBean requestBean = new GetBookBytesRequestBean();
        requestBean.uniqueId = uniqueId;
        requestBean.type = type;
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxGetBookBytesRequest request = new RxGetBookBytesRequest(requestBean);
        request.execute(new RxCallback<RxGetBookBytesRequest>() {
            @Override
            public void onNext(RxGetBookBytesRequest request) {
                ResponseBody body = request.getResult();
                try {
                    assertTrue(body.bytes().length > 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void testGetBookCommentList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookList();
        final String uniqueId = productResult.list.get(0).getGuid();
        GetBookCommentListRequestBean requestBean = new GetBookCommentListRequestBean();
        requestBean.uniqueId = uniqueId;
        requestBean.params = null;
        RxGetBookCommentListRequest request = new RxGetBookCommentListRequest(requestBean);
        request.execute(new RxCallback<RxGetBookCommentListRequest>() {
            @Override
            public void onNext(RxGetBookCommentListRequest request) {
                ProductResult<Comment> resut = request.getResult();
                assertNotNull(resut);
                commentList = resut.list;
                assertNotNull(commentList);
                assertTrue(commentList.size() >= 0);
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

    public void testGetBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookCommentList();
        final String uniqueId = productResult.list.get(0).getGuid();
        final Comment comment = commentList.get(0);
        String commentId = comment.getGuid();
        GetBookCommentRequestBean requestBean = new GetBookCommentRequestBean();
        requestBean.bookId = uniqueId;
        requestBean.commentId = commentId;
        RxGetBookCommentRequest request = new RxGetBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxGetBookCommentRequest>() {
            @Override
            public void onNext(RxGetBookCommentRequest request) {
                Comment resut = request.getResult();
                assertNotNull(resut);
                assertEquals(resut.title, comment.title);
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

    public void testPostBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testGetBookList();
        final Comment commentEntity = new Comment("Hello", "Nice to meent you!");
        final String uniqueId = productResult.list.get(0).getGuid();
        PostBookCommentRequestBean requestBean = new PostBookCommentRequestBean();
        requestBean.bookId = uniqueId;
        requestBean.comment = commentEntity;
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxPostBookCommentRequest request = new RxPostBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxPostBookCommentRequest>() {
            @Override
            public void onNext(RxPostBookCommentRequest request) {
                postComment = request.getResult();
                assertNotNull(postComment);
                assertEquals(postComment.productId, uniqueId);
                assertEquals(postComment.title, commentEntity.title);
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

    public void testUpdataBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testPostBookComment();
        final Comment testComment = new Comment();
        final String uniqueId = productResult.list.get(0).getGuid();
        UpdataBookCommentRequestBean requestBean = new UpdataBookCommentRequestBean();
        testComment.content = postComment.content + "I'm fine,thanks!";
        testComment.title = postComment.title;
        testComment.rating = 4;
        requestBean.bookId = uniqueId;
        requestBean.commentId = postComment.getGuid();
        requestBean.comment = testComment;
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxUpdataBookCommentRequest request = new RxUpdataBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxUpdataBookCommentRequest>() {
            @Override
            public void onNext(RxUpdataBookCommentRequest request) {
                Comment updateComment = request.getResult();
                assertNotNull(updateComment);
                assertEquals(updateComment.getGuid(), postComment.getGuid());
                assertEquals(updateComment.content, testComment.content);
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

    public void testSupportBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testPostBookComment();
        final String uniqueId = productResult.list.get(0).getGuid();
        SupportBookCommentRequestBean requestBean = new SupportBookCommentRequestBean();
        requestBean.bookId = uniqueId;
        requestBean.commentId = postComment.getGuid();
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxSupportBookCommentRequest request = new RxSupportBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxSupportBookCommentRequest>() {
            @Override
            public void onNext(RxSupportBookCommentRequest request) {
                Comment supportComment = request.getResult();
                assertNotNull(supportComment);
                assertTrue(supportComment.upCount > postComment.upCount);
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

    public void testDisagreeBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testPostBookComment();
        final String uniqueId = productResult.list.get(0).getGuid();
        DisagreeBookCommentRequestBean requestBean = new DisagreeBookCommentRequestBean();
        requestBean.bookId = uniqueId;
        requestBean.commentId = postComment.getGuid();
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxDisagreeBookCommentRequest request = new RxDisagreeBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxDisagreeBookCommentRequest>() {
            @Override
            public void onNext(RxDisagreeBookCommentRequest request) {
                Comment disagreeComment = request.getResult();
                assertNotNull(disagreeComment);
                assertTrue(disagreeComment.downCount > postComment.downCount);
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

    public void testDeleteBookComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testPostBookComment();
        final String uniqueId = productResult.list.get(0).getGuid();
        DeleteBookCommentRequestBean requestBean = new DeleteBookCommentRequestBean();
        requestBean.bookId = uniqueId;
        requestBean.commentId = postComment.getGuid();
        requestBean.sessionToken = getCurrentAccount().sessionToken;
        RxDeleteBookCommentRequest request = new RxDeleteBookCommentRequest(requestBean);
        request.execute(new RxCallback<RxDeleteBookCommentRequest>() {
            @Override
            public void onNext(RxDeleteBookCommentRequest request) {
                ResponseBody body = request.getResult();
                assertNotNull(body);
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

    public void testBookRecommendedList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ProductQuery query = new ProductQuery();
        query.count = 6;
        RxBookRecommendedListRequest request = new RxBookRecommendedListRequest(JSON.toJSONString(query));
        request.execute(new RxCallback<RxBookRecommendedListRequest>() {
            @Override
            public void onNext(RxBookRecommendedListRequest request) {
                ProductResult<Product> productResult = request.getResult();
                assertNotNull(productResult);
                List<Product> list = productResult.list;
                assertNotNull(list);
                assertTrue(list.size() <= query.count);
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

    public void testBookListWithProductQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        int count = TestUtils.randInt(4, 8);
        final ProductQuery productQuery = new ProductQuery();
        productQuery.count = count;
        productQuery.sortOrder = false;
        RxBookListWithProductRequest request = new RxBookListWithProductRequest(JSON.toJSONString(productQuery));
        request.execute(new RxCallback<RxBookListWithProductRequest>() {
            @Override
            public void onNext(RxBookListWithProductRequest request) {
                ProductResult<Product> productResult = request.getResult();
                assertNotNull(productResult);
                List<Product> list = productResult.list;
                assertNotNull(list);
                assertNotNull(productResult.count > 0);
                assertTrue(list.size() <= productQuery.count);
                Product tmp = list.get(0);
                for (int i = 0; i < list.size(); i++) {
                    assertTrue(tmp.getCreatedAt().getTime() >= list.get(i).getCreatedAt().getTime());
                    tmp = list.get(i);
                }
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

    public void testBookContainerList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RxBookContainerListRequest request = new RxBookContainerListRequest();
        request.execute(new RxCallback<RxBookContainerListRequest>() {
            @Override
            public void onNext(RxBookContainerListRequest request) {
                bookContainerList = request.getResult();
                assertNotNull(bookContainerList);
                assertTrue(bookContainerList.size() > 0);
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

    public void testBookContainer() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testBookContainerList();
        RxBookContainerRequest request = new RxBookContainerRequest(bookContainerList.get(0).getGuid());
        request.execute(new RxCallback<RxBookContainerRequest>() {
            @Override
            public void onNext(RxBookContainerRequest request) {
                Category category = request.getResult();
                assertNotNull(category);
                assertEquals(category.name, bookContainerList.get(0).name);
                if (category.children != null && category.children.size() > 0) {
                    for (Category child : category.children) {
                        assertEquals(child.parentGuid, category.getGuid());
                    }
                }
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

    public void testProductSharedList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testBookContainerList();
        BaseQuery query = new BaseQuery();
        query.sortOrder = false;
        query.sortBy = "shareDate";
        int testCount = 0;
        while (testCount != 10) {
            runProductSharedListTest(query);
            query.offset += query.count;
            testCount++;
        }
        countDownLatch.countDown();
        countDownLatch.await();
    }

    public void runProductSharedListTest(BaseQuery query) throws Exception {
        RxProductShareListRequest request = new RxProductShareListRequest(JSON.toJSONString(query));
        request.execute(new RxCallback<RxProductShareListRequest>() {
            @Override
            public void onNext(RxProductShareListRequest request) {
                ProductResult<ProductShared> result = request.getResult();
                assertNotNull(result);
                List<ProductShared> list = result.list;
                assertNotNull(list);
                if (list.size() > 0) {
                    ProductShared tmp = list.get(0);
                    for (int i = 0; i < list.size(); i++) {
                        assertNotNull(tmp.productId);
                        assertNotNull(tmp.productTitle);
                        assertNotNull(tmp.productName);
                        assertTrue(tmp.productSize > 0);
                        assertNotNull(tmp.sharerId);
                        assertNotNull(tmp.sharerName);
                        assertTrue(tmp.shareDate.getTime() >= list.get(i).shareDate.getTime());
                        tmp = list.get(i);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
            }
        });
    }
}