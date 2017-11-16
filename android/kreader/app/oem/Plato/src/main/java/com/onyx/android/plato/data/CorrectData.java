package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.GetCorrectedTaskRequest;
import com.onyx.android.plato.requests.local.GetAllQuestionRequest;
import com.onyx.android.plato.requests.local.ResolveAdapterDataRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/10/25.
 */

public class CorrectData {
    public void getCorrectData(GetCorrectedTaskRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getAnswers(GetAllQuestionRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void resolveData(ResolveAdapterDataRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
