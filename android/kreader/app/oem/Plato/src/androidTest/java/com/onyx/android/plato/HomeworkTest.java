package com.onyx.android.plato;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskRequestBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskResultBean;
import com.onyx.android.plato.cloud.bean.GetSubjectBean;
import com.onyx.android.plato.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkRequestBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.cloud.bean.IntrospectionRequestBean;
import com.onyx.android.plato.cloud.bean.IntrospectionBean;
import com.onyx.android.plato.cloud.bean.PracticeAnswerBean;
import com.onyx.android.plato.cloud.bean.PracticeFavoriteBean;
import com.onyx.android.plato.cloud.bean.PracticeFavoriteOrDeleteBean;
import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeRequestBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.bean.TaskBean;
import com.onyx.android.plato.cloud.bean.UploadBean;
import com.onyx.android.plato.requests.cloud.FavoriteOrDeletePracticeRequest;
import com.onyx.android.plato.requests.cloud.GetCorrectedTaskRequest;
import com.onyx.android.plato.requests.cloud.GetExerciseTypeRequest;
import com.onyx.android.plato.requests.cloud.GetPracticeParseRequest;
import com.onyx.android.plato.requests.cloud.GetSubjectRequest;
import com.onyx.android.plato.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.plato.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.plato.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.plato.requests.cloud.PracticeIntrospectionRequest;
import com.onyx.android.plato.requests.cloud.RequestUploadFile;
import com.onyx.android.plato.requests.cloud.SubmitPracticeRequest;
import com.onyx.android.plato.requests.cloud.TaskDetailRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;
import com.onyx.android.plato.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

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
        requestBean.studentId = "1";
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

    public void testSubmitPractice() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        SubmitPracticeRequestBean submitPracticeRequestBean = new SubmitPracticeRequestBean();

        ArrayList<PracticeAnswerBean> requestList = new ArrayList<>();
        PracticeAnswerBean practiceAnswerBean = new PracticeAnswerBean();
        practiceAnswerBean.id = 1523;
        practiceAnswerBean.answer = "ce shi a";
        requestList.add(practiceAnswerBean);
        String jsonString = JSON.toJSONString(requestList);
        submitPracticeRequestBean.id = 61;
        submitPracticeRequestBean.studentId = 104 ;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonString);
        submitPracticeRequestBean.practiceListBody = requestBody;

        final SubmitPracticeRequest submitPracticeRequest = new SubmitPracticeRequest(submitPracticeRequestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), submitPracticeRequest, new BaseCallback() {
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

    public void testPracticeFavoriteOrDelete() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PracticeFavoriteBean bean = new PracticeFavoriteBean();
        bean.id = 1523;
        bean.pid = 12;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(bean));
        PracticeFavoriteOrDeleteBean requestBean = new PracticeFavoriteOrDeleteBean();
        requestBean.studentId = 108;
        requestBean.requestBody = requestBody;
        final FavoriteOrDeletePracticeRequest rq = new FavoriteOrDeletePracticeRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetCorrectedTask() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetCorrectedTaskRequestBean requestBean = new GetCorrectedTaskRequestBean();
        requestBean.practiceId = 25;
        requestBean.studentId = 108;
        final GetCorrectedTaskRequest rq = new GetCorrectedTaskRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetCorrectedTaskResultBean taskBean = rq.getTaskBean();
                assertNotNull(taskBean);
                assertNotNull(taskBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testPracticeParse() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PracticeParseRequestBean requestBean = new PracticeParseRequestBean();
        requestBean.id = 1523;
        requestBean.pid = 1;
        requestBean.studentId = 105;
        final GetPracticeParseRequest rq = new GetPracticeParseRequest(requestBean);

        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PracticeParseResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetStudyReportDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetStudyReportDetailRequest rq = new GetStudyReportDetailRequest(1, 1);
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

    public void testPracticeIntrospection() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        IntrospectionBean introspectionBean = new IntrospectionBean();
        ArrayList<String> recordPath = new ArrayList<>();
        recordPath.add("http://baidu.com/a.mp3");
        introspectionBean.audioUrls = recordPath;
        IntrospectionRequestBean requestBean = new IntrospectionRequestBean();
        requestBean.id = 328;
        requestBean.requestBody = RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(introspectionBean));

        final PracticeIntrospectionRequest rq = new PracticeIntrospectionRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetSubject() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetSubjectRequest rq = new GetSubjectRequest(107);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectBean subjects = rq.getSubjects();
                assertNotNull(subjects);
                assertNotNull(subjects.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetExerciseType() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetExerciseTypeRequest rq = new GetExerciseTypeRequest();
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectBean exerciseTypes = rq.getExerciseTypes();
                assertNotNull(exerciseTypes);
                assertNotNull(exerciseTypes.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testUploadFile() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Bitmap bitmap = BitmapFactory.decodeResource(SunApplication.getInstance().getResources(), R.drawable.book_cover);
        File file = Utils.bitmap2File(bitmap);

        final RequestUploadFile rq = new RequestUploadFile(file);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UploadBean uploadBean = rq.getBean();
                assertNotNull(uploadBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
