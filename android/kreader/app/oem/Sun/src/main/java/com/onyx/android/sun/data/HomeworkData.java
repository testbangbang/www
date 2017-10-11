package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkData {
    public void getHomeworkUnfinishedData(HomeworkUnfinishedRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), rq, callback);
    }

    public void getHomeworkFinishedData(HomeworkFinishedRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), rq, callback);
    }
}
