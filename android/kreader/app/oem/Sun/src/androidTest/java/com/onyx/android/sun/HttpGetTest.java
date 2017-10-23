package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.data.HomeworkData;
import com.onyx.android.sun.data.MainActData;
import com.onyx.android.sun.requests.GetNewMessageRequest;
import com.onyx.android.sun.requests.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

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

        MainActData mainActData = new MainActData();
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.page = "1";
        requestBean.size = "2";
        requestBean.studentId = "2";
        final GetNewMessageRequest rq = new GetNewMessageRequest(requestBean);

        mainActData.getNewMessage(rq, new BaseCallback() {
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
