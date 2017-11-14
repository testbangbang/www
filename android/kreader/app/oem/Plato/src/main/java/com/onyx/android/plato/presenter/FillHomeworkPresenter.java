package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.data.FillHomeworkData;
import com.onyx.android.plato.requests.local.FillAnswerRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

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
