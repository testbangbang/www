package com.onyx.android.plato;

import android.test.ApplicationTestCase;

import com.onyx.android.plato.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkRequestBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.data.HomeworkData;
import com.onyx.android.plato.data.MainActivityData;
import com.onyx.android.plato.requests.cloud.GetNewMessageRequest;
import com.onyx.android.plato.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jackdeng on 17-10-20.
 */

public class HttpGetTest extends ApplicationTestCase<SunApplication> {
    public HttpGetTest() {
        super(SunApplication.class);
    }

    public void testHomeworkFinished() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HomeworkData homeworkData = new HomeworkData();
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.FINISHED_STATE;
        requestBean.course = null;
        requestBean.endtime = null;
        requestBean.starttime = null;
        requestBean.studentId = "2";
        requestBean.type = null;
        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);

        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkFinishedResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetMessage() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MainActivityData mainActivityData = new MainActivityData();
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.page = "1";
        requestBean.size = "2";
        requestBean.studentId = "2";
        final GetNewMessageRequest rq = new GetNewMessageRequest(requestBean);

        mainActivityData.getNewMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getHomeworkUnfinishedResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
