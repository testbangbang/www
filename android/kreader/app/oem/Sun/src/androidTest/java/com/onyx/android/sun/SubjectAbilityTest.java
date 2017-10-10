package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.sun.cloud.bean.PracticesRequestBean;
import com.onyx.android.sun.cloud.bean.PracticesResultBean;
import com.onyx.android.sun.requests.MessagesRequest;
import com.onyx.android.sun.requests.SubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

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
        final SubjectAbilityRequest rq = new SubjectAbilityRequest("1");
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PersonalAbilityResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
