package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.sun.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.TaskDetailRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

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
}
