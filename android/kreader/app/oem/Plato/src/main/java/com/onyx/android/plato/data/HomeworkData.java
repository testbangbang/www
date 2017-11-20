package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.GetExerciseTypeRequest;
import com.onyx.android.plato.requests.cloud.GetReportListRequest;
import com.onyx.android.plato.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.plato.requests.cloud.GetSubjectRequest;
import com.onyx.android.plato.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.plato.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.plato.requests.cloud.TaskDetailRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkData {
    public void getHomeworkUnfinishedData(HomeworkUnfinishedRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getHomeworkFinishedData(HomeworkFinishedRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getTaskDetail(TaskDetailRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getStudyReportDetail(GetStudyReportDetailRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getReportList(GetReportListRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getSubjects(GetSubjectRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getExerciseType(GetExerciseTypeRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
