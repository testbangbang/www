package com.onyx.edu.homework.db;

import com.onyx.android.sdk.data.model.QuestionReview;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

@Table(database = HomeworkDatabase.class)
public class QuestionModel extends BaseModel {

    @PrimaryKey
    @Column
    @Unique
    private String uniqueId;

    @Column
    private String homeworkId;

    @Column
    private String questionId;

    @Column(typeConverter = ConverterListString.class)
    private List values;

    @Column(typeConverter = ConverterListString.class)
    private List answer;

    @Column(typeConverter = ConverterQuestionReview.class)
    private QuestionReview review;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List getAnswer() {
        return answer;
    }

    public void setAnswer(List answer) {
        this.answer = answer;
    }

    public QuestionReview getReview() {
        return review;
    }

    public void setReview(QuestionReview review) {
        this.review = review;
    }

    public static QuestionModel create(String uniqueId, String questionId, String homeworkId) {
        QuestionModel model = new QuestionModel();
        model.setQuestionId(questionId);
        model.setUniqueId(uniqueId);
        model.setHomeworkId(homeworkId);
        return model;
    }
}
