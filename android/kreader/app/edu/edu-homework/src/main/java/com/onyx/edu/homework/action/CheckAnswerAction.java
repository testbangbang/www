package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.CheckAnswerRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class CheckAnswerAction extends BaseAction {

    private List<Question> questions;
    private String homeworkId;

    public CheckAnswerAction(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        final CheckAnswerRequest answerRequest = new CheckAnswerRequest(questions, context, homeworkId);
        getDataManager().submit(context, answerRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = answerRequest.getQuestions();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
