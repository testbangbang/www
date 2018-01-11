package com.onyx.edu.homework.db;

import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.onyx.android.sdk.data.model.homework.QuestionReview;
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

    @Column
    private String content;

    @Column
    private String answerContent;

    @Column
    private String analysis;

    @Column
    private int type;

    @Column
    private int difficulty;

    @Column(typeConverter = ConverterListQuestionOption.class)
    private List options;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public static QuestionModel create(String uniqueId, String questionId, String homeworkId) {
        QuestionModel model = new QuestionModel();
        model.setQuestionId(questionId);
        model.setUniqueId(uniqueId);
        model.setHomeworkId(homeworkId);
        return model;
    }

    public void loadFromQuestion(Question question) {
        if (question == null) {
            return;
        }
        setContent(question.content);
        setAnswerContent(question.answers);
        setAnalysis(question.analysis);
        setType(question.QuesType);
        setDifficulty(question.difficulty);
        setOptions(question.options);
        setAnswer(question.correctOptions);
    }
}
