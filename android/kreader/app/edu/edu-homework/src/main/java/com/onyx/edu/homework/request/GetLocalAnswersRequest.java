package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.QuestionModel;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class GetLocalAnswersRequest extends BaseDataRequest {

    private volatile List<Question> questions;
    private String homeworkId;

    public GetLocalAnswersRequest(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                unCheckOption(question.options);
                QuestionModel model = DBDataProvider.loadQuestion(homeworkId, question._id);
                setOptionAnswer(question, model);
            }
        }
    }

    private void unCheckOption(List<QuestionOption> options) {
        if (options == null) {
            return;
        }
        for (QuestionOption option : options) {
            option.setChecked(false);
        }
    }

    private void setOptionAnswer(Question question, QuestionModel model) {
        if (model == null) {
            return;
        }
        List<String> values = model.getValues();
        if (values == null) {
            return;
        }
        List<QuestionOption> options = question.options;
        if (options == null) {
            return;
        }
        for (String value : values) {
            QuestionOption option = findQuestionOption(options, value);
            if (option != null) {
                option.setChecked(true);
            }
        }
    }

    private QuestionOption findQuestionOption(@NonNull List<QuestionOption> options, String optionId) {
        for (QuestionOption option : options) {
            if (option._id.equals(optionId)) {
                return option;
            }
        }
        return null;
    }
}
