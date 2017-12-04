package com.onyx.android.plato;

import android.test.ApplicationTestCase;

import com.onyx.android.plato.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.plato.requests.cloud.SubjectAbilityRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-10-10.
 */

public class SubjectAbilityTest extends ApplicationTestCase<SunApplication> {
    public SubjectAbilityTest() {
        super(SunApplication.class);
    }

    public void testAbility() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final SubjectAbilityRequest rq = new SubjectAbilityRequest();
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PersonalAbilityResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
