package com.onyx.android.sdk.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/11/24.
 */

public class Question implements Serializable {

    public String _id;
    public String uniqueId;
    public String content;
    public String answers;
    public String analysis;
    public List<String> correctOptions;
    public int QuesType;
    public int difficulty;
    public List<QuestionOption> options;
    public QuestionReview review;

    public boolean doneAnswer;

    public String getUniqueId() {
        return uniqueId;
    }

    public String getQuestionId() {
        return _id;
    }

    public void setReview(QuestionReview review) {
        this.review = review;
    }

    public void setCorrectOptions(List<String> correctOptions) {
        this.correctOptions = correctOptions;
    }

    public QuestionReview getReview() {
        return review;
    }

    public QuestionType getType() {
        int index= QuesType - 1;
        if (index >= QuestionType.values().length) {
            return QuestionType.SINGLE;
        }
        return QuestionType.values()[index];
    }

    public boolean isChoiceQuestion() {
        return getType() == QuestionType.SINGLE || getType() == QuestionType.MULTIPLE || getType() == QuestionType.JUDGMENT;
    }

    public boolean isSingleChoiceQuestion() {
        return getType() == QuestionType.SINGLE || getType() == QuestionType.JUDGMENT;
    }

    public boolean isMultipleChoiceQuestion() {
        return getType() == QuestionType.MULTIPLE;
    }

    public void setDoneAnswer(boolean doneAnswer) {
        this.doneAnswer = doneAnswer;
    }

    public HomeworkSubmitAnswer createAnswer() {
        if (isSingleChoiceQuestion()) {
            return createSingleAnswer();
        }
        if (isMultipleChoiceQuestion()) {
            return createMultipleAnswers();
        }
        return createFillAnswer();
    }

    public HomeworkSubmitAnswer createSingleAnswer() {
        HomeworkSubmitAnswer answer = new HomeworkSubmitAnswer();
        for (QuestionOption option : options) {
            if (option.checked) {
                answer.setQuestion(getQuestionId());
                answer.addValue(option._id);
                answer.setUniqueId(getUniqueId());
            }
        }
        return answer;
    }

    public HomeworkSubmitAnswer createMultipleAnswers() {
        HomeworkSubmitAnswer answer = new HomeworkSubmitAnswer();
        for (QuestionOption option : options) {
            if (option.checked) {
                answer.setQuestion(getQuestionId());
                answer.addValue(option._id);
                answer.setUniqueId(getUniqueId());
            }
        }
        return answer;
    }

    public HomeworkSubmitAnswer createFillAnswer() {
        HomeworkSubmitAnswer answer = new HomeworkSubmitAnswer();
        answer.setQuestion(getQuestionId());
        answer.setUniqueId(getUniqueId());
        return answer;
    }
}
