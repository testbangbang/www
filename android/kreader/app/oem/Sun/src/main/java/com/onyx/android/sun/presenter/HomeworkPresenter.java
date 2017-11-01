package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.bean.TaskBean;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.data.HomeworkData;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.sun.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.TaskDetailRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.List;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkPresenter {
    private HomeworkView homeworkView;
    private HomeworkData homeworkData;

    public HomeworkPresenter(HomeworkView homeworkView) {
        this.homeworkView = homeworkView;
        homeworkData = new HomeworkData();
    }

    public void getHomeworkUnfinishedData() {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.UNFINISHED_STATE;
        requestBean.studentId = "2";
        final HomeworkUnfinishedRequest rq = new HomeworkUnfinishedRequest(requestBean);
        homeworkData.getHomeworkUnfinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    return;
                }
                List<ContentBean> content = resultBean.data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setUnfinishedData(content);
                }
            }
        });
    }

    public void getHomeworkFinishedData(String course, String startTime, String endTime, String type) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.FINISHED_STATE;
        requestBean.course = course;
        requestBean.endtime = endTime;
        requestBean.starttime = startTime;
        requestBean.studentId = "2";
        requestBean.type = type;

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkFinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                    return;
                }

                HomeworkFinishedResultBean.FinishData data = resultBean.data;
                List<FinishContent> content = data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setFinishedData(content);
                }
            }
        });
    }

    public void getStudyReportData(String course) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.REPORT_STATE;
        requestBean.course = course;
        requestBean.studentId = "2";

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkFinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                    return;
                }

                HomeworkFinishedResultBean.FinishData data = resultBean.data;
                List<FinishContent> content = data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setReportData(content);
                }
            }
        });
    }

    public void getTaskDetail(int id) {
        final TaskDetailRequest rq = new TaskDetailRequest(1);
        homeworkData.getTaskDetail(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                TaskBean taskBean = rq.getTaskBean();
                if(taskBean == null) {
                    return;
                }

                if (taskBean.data != null) {
                    homeworkView.setTaskDetail(taskBean.data);
                }
            }
        });
    }

    public void getStudyReportDetail(int id) {
        final GetStudyReportDetailRequest rq = new GetStudyReportDetailRequest(id);
        homeworkData.getStudyReportDetail(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetStudyReportDetailResultBean resultBean = rq.getStudyReportDetailResultBean();
                if(resultBean == null) {
                    return;
                }

                if (resultBean.data != null) {
                    homeworkView.setStudyReportDetail(resultBean.data);
                }
            }
        });
    }
}
