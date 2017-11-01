package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.bean.TaskBean;
import com.onyx.android.sun.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.sun.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.TaskDetailRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2017/10/10.
 */

public class HomeworkTest extends ApplicationTestCase<SunApplication> {
    public HomeworkTest() {
        super(SunApplication.class);
    }

    public void testHomeworkUnfinished() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.course = "1";
        requestBean.endtime = "2017-09-10";
        requestBean.page = "1";
        requestBean.size = "10";
        requestBean.starttime = "2017-02-02";
        requestBean.status = "tbd";//completed  report
        requestBean.studentId = "2";
        requestBean.type = "all";

        final HomeworkUnfinishedRequest rq = new HomeworkUnfinishedRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testHomeworkFinished() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.course = "1";
        requestBean.endtime = "2017-09-10";
        requestBean.page = "1";
        requestBean.size = "10";
        requestBean.starttime = "2017-02-02";
        requestBean.status = "completed";
        requestBean.studentId = "2";
        requestBean.type = "all";

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
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

    public void testStudyReport() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.course = "1";
        requestBean.endtime = "2017-09-10";
        requestBean.page = "1";
        requestBean.size = "10";
        requestBean.starttime = "2017-02-02";
        requestBean.status = "report";
        requestBean.studentId = "2";
        requestBean.type = "all";

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
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

    public void testGetTaskDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TaskDetailRequest rq = new TaskDetailRequest(1);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                TaskBean taskBean = rq.getTaskBean();
                assertNotNull(taskBean);
                assertNotNull(taskBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetStudyReportDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetStudyReportDetailRequest rq = new GetStudyReportDetailRequest(1);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(),rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetStudyReportDetailResultBean resultBean = rq.getStudyReportDetailResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
