package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sun.cloud.bean.PracticeAnswerBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeRequestBean;
import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.cloud.bean.UserLoginResultBean;
import com.onyx.android.sun.data.UserLoginActData;
import com.onyx.android.sun.requests.SubmitPracticeRequest;
import com.onyx.android.sun.requests.UserLoginRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by jackdeng on 17-10-20.
 */

public class UserTest extends ApplicationTestCase<SunApplication> {
    public UserTest() {
        super(SunApplication.class);
    }

    public void testUserLogin() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UserLoginActData userLoginData = new UserLoginActData();
        UserLoginRequestBean requestBean = new UserLoginRequestBean();
        requestBean.account = "123456";
        requestBean.md5Password = "321";
        final UserLoginRequest rq = new UserLoginRequest(requestBean);

        userLoginData.userLogin(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLoginResultBean resultBean = rq.getLoginResultBean();
                assertEquals(0,resultBean.code);
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testHttpPostApi() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        SubmitPracticeRequestBean submitPracticeRequestBean = new SubmitPracticeRequestBean();

        ArrayList<PracticeAnswerBean> requestList = new ArrayList<>();
        PracticeAnswerBean practiceAnswerBean = new PracticeAnswerBean();
        practiceAnswerBean.id = 1;
        List<String> answerList = new ArrayList<>();
        answerList.add("A");
        answerList.add("B");
        practiceAnswerBean.answer = answerList;
        requestList.add(practiceAnswerBean);
        String jsonString = JSON.toJSONString(requestList);

        submitPracticeRequestBean.id = 1;
        submitPracticeRequestBean.studentId = 2 ;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonString);
        submitPracticeRequestBean.practiceListBody = requestBody;

        final SubmitPracticeRequest submitPracticeRequest = new SubmitPracticeRequest(submitPracticeRequestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), submitPracticeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean loginResultBean = submitPracticeRequest.getResult();
                assertNotNull(loginResultBean);
                assertEquals("ok",loginResultBean.msg);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

}
