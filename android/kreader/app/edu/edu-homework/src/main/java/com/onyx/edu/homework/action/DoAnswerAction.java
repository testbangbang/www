package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.DoAnswerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class DoAnswerAction extends BaseAction {

    private Question question;
    private String homeworkId;

    public DoAnswerAction(Question question, String homeworkId) {
        this.question = question;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        List<QuestionOption> options = question.options;
        List<String> values = new ArrayList<>();
        for (QuestionOption option : options) {
            if (option.checked) {
                values.add(option._id);
            }
        }
        DoAnswerRequest answerRequest = new DoAnswerRequest(values, question._id, homeworkId);
        getDataManager().submit(context, answerRequest, baseCallback);
    }
}
