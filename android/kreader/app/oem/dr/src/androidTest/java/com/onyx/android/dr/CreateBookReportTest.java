package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.request.cloud.AddCommentRequest;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.dr.request.cloud.GetBookReportRequest;
import com.onyx.android.dr.request.cloud.LoginByAdminRequest;
import com.onyx.android.dr.request.cloud.RemoveCommentRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.AddCommentRequestBean;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.CreateBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;

import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2017/9/18.
 */

public class CreateBookReportTest extends ApplicationTestCase<DRApplication> {
    public CreateBookReportTest() {
        super(DRApplication.class);
    }

    public void testLogin() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final BaseAuthAccount neoAccountBase = BaseAuthAccount.create("15112286351@163.com", "hehai3389");
        final LoginByAdminRequest req = new LoginByAdminRequest(neoAccountBase);
        final CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.addRequest(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                NeoAccountBase neoAccount = req.getNeoAccount();
                assertNotNull(neoAccount);
                countDownLatch.countDown();
            }
        });
        requestChain.execute(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager());
        countDownLatch.await();
    }

    public void testCreateBookReport() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CreateBookReportRequestBean requestBean = new CreateBookReportRequestBean();
        //requestBean.setBook("123456789");
        requestBean.setContent("测试测试测试");
        requestBean.setName("dbflow教材");

        final CreateBookReportRequest rq = new CreateBookReportRequest(requestBean);
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getCreateBookReportResult();
                assertNotNull(result);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetImpressionList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetBookReportListRequestBean requstBean = new GetBookReportListRequestBean();
        requstBean.offset = "1";
        requstBean.limit = "4";
        requstBean.order = "1";
        requstBean.sortBy = "createdAt";

        final GetBookReportListRequest rq = new GetBookReportListRequest(requstBean);
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetBookReportList bookReportList = rq.getBookReportList();
                assertNotNull(bookReportList);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetImpression() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetBookReportRequest rq = new GetBookReportRequest("59c0bed22552052097e1ef9f");
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getResult();
                assertNotNull(result);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testDeleteImpression() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final DeleteBookReportRequest rq = new DeleteBookReportRequest("59c1d06215ec447a036f7524");
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String result = rq.getResult();
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testAddComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AddCommentRequestBean requestBean = new AddCommentRequestBean();
        requestBean.top = "10";
        requestBean.left = "10";
        requestBean.content = "哈撒key";

        final AddCommentRequest rq = new AddCommentRequest("59c1046c127aed7b9b9f4b48", requestBean);
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getResult();
                assertNotNull(result);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testRemoveComment() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final RemoveCommentRequest rq = new RemoveCommentRequest("59c1046c127aed7b9b9f4b48", "59c21ba31263013c0542b056");
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getResult();
                assertNotNull(result);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
