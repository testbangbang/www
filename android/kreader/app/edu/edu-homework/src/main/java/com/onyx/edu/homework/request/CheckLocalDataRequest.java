package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.android.sdk.data.model.QuestionReview;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;
import com.onyx.edu.homework.db.QuestionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class CheckLocalDataRequest extends BaseDataRequest {

    private volatile List<Question> questions;
    private String homeworkId;
    private HomeworkState currentState = HomeworkState.DOING;

    public CheckLocalDataRequest(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        getLocalAnswer();
        checkHomeworkState();
    }

    private void getLocalAnswer() {
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                QuestionModel model = DBDataProvider.loadQuestion(question.getUniqueId());
                if (model == null) {
                    model = QuestionModel.create(question.getUniqueId(),
                            question.getQuestionId(),
                            homeworkId);
                }
                // 1. get right answer
                // 2. un check option
                // 3. set option answer
                setRightAnswer(question, model);
                unCheckOption(question.options);
                setOptionAnswer(question, model);
                DBDataProvider.saveQuestion(model);
            }
        }
    }

    private void checkHomeworkState() {
        HomeworkModel homeworkModel = DBDataProvider.loadHomework(homeworkId);
        if (homeworkModel == null) {
            homeworkModel = HomeworkModel.create(homeworkId);
        }
        int state = homeworkModel.getState();
        currentState = HomeworkState.getHomeworkState(state);
    }

    private void setRightAnswer(Question question, QuestionModel model) {
        List<QuestionOption> options = question.options;
        if (options == null) {
            return;
        }
        List<String> answer = new ArrayList<>();
        for (QuestionOption option : options) {
            if (option.checked) {
                answer.add(option._id);
            }
        }
        model.setAnswer(answer);
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

    public HomeworkState getCurrentState() {
        return currentState;
    }
}
