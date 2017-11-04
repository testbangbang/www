package com.onyx.android.sun;


import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.GetSubjectAbilityRequestBean;
import com.onyx.android.sun.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.sun.data.GoalAdvancedFragmentData;
import com.onyx.android.sun.requests.cloud.GetSubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jackdeng on 2017/11/1.
 */

public class GoalAdvancedTest extends ApplicationTestCase<SunApplication> {

    public GoalAdvancedTest() {
        super(SunApplication.class);
    }

    public void testGetSubjectAbility() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GoalAdvancedFragmentData goalAdvancedFragmentData = new GoalAdvancedFragmentData();
        GetSubjectAbilityRequestBean requestBean = new GetSubjectAbilityRequestBean();
        requestBean.id = "108";
        requestBean.course = "1";
        requestBean.term = "89";
        final GetSubjectAbilityRequest rq = new GetSubjectAbilityRequest(requestBean);
        goalAdvancedFragmentData.getSubjectAbility(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectAbilityResultBean resultBean = rq.getResultBean();
                assertEquals(0, resultBean.code);
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
