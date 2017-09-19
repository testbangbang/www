package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.activity.LoginView;
import com.onyx.android.dr.bean.CityBean;
import com.onyx.android.dr.bean.InterestBean;
import com.onyx.android.dr.bean.ProvinceBean;
import com.onyx.android.dr.presenter.LoginPresenter;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.LoginByAdminRequest;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.CreateBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;

import java.util.List;
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
        requestBean.setBook("123456789");
        requestBean.setContent("啊公交价格萨斯爱家乐福撒酒疯三国杀");
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
        CountDownLatch countDownLatch = new CountDownLatch(1);

    }
}
