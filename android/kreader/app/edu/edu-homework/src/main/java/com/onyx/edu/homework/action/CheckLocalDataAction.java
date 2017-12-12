package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.request.CheckLocalDataRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class CheckLocalDataAction extends BaseAction {

    private volatile List<Question> questions;
    private String homeworkId;
    private HomeworkState currentState;

    public CheckLocalDataAction(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final CheckLocalDataRequest localAnswersRequest = new CheckLocalDataRequest(questions, homeworkId);
        getDataManager().submit(context, localAnswersRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                currentState = localAnswersRequest.getCurrentState();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public HomeworkState getCurrentState() {
        return currentState;
    }
}
