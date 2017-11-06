package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.data.FillHomeworkData;
import com.onyx.android.sun.requests.local.FillAnswerRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

/**
 * Created by li on 2017/10/20.
 */

public class FillHomeworkPresenter {
    private FillHomeworkData fillHomeworkData;

    public FillHomeworkPresenter() {
        fillHomeworkData = new FillHomeworkData();
    }

    public void insertAnswer(Question question) {
        //TODO:to complete params
        /*FillAnswerRequest rq = new FillAnswerRequest("1", question.id + "", question.type, question.question, question.userAnswer);
        fillHomeworkData.insertAnswer(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });*/
    }
}
