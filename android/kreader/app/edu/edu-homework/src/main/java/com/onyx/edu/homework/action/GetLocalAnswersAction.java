package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.GetLocalAnswersRequest;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class GetLocalAnswersAction extends BaseAction {

    private volatile List<Question> questions;
    private String homeworkId;

    public GetLocalAnswersAction(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        GetLocalAnswersRequest localAnswersRequest = new GetLocalAnswersRequest(questions, homeworkId);
        getDataManager().submit(context, localAnswersRequest, baseCallback);
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
