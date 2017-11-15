package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.SubmitPracticeRequest;
import com.onyx.android.plato.requests.local.FillAnswerRequest;
import com.onyx.android.plato.requests.local.GetAllQuestionRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/10/20.
 */

public class FillHomeworkData {

    public void insertAnswer(FillAnswerRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getAllQuestion(GetAllQuestionRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void submitAnswers(SubmitPracticeRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
