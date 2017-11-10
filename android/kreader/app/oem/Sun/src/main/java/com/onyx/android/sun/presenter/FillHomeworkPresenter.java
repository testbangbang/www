package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.cloud.bean.QuestionViewBean;
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

    public void insertAnswer(int taskId, QuestionViewBean questionViewBean) {
        //TODO:to complete params
        FillAnswerRequest rq = new FillAnswerRequest(taskId + "", questionViewBean.getId() + "", questionViewBean.getContent(), questionViewBean.getShowType(), questionViewBean.getUserAnswer());
        fillHomeworkData.insertAnswer(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }
}
