package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.QuestionModel;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class DoAnswerRequest extends BaseDataRequest {

    private List<String> values;
    private String questionUniqueId;
    private String questionId;
    private String homeworkId;

    public DoAnswerRequest(List<String> values, String questionUniqueId, String questionId, String homeworkId) {
        this.values = values;
        this.questionUniqueId = questionUniqueId;
        this.homeworkId = homeworkId;
        this.questionId = questionId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        QuestionModel question = DBDataProvider.loadQuestion(questionUniqueId);
        if (question == null) {
            question = new QuestionModel();
            question.setUniqueId(questionUniqueId);
            question.setHomeworkId(homeworkId);
            question.setQuestionId(questionId);
        }
        question.setValues(values);
        DBDataProvider.saveQuestion(question);
    }
}
