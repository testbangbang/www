package com.onyx.android.sdk.data.model;

/**
 * Created by lxm on 2017/12/13.
 */

public class QuestionReview {

    public boolean correct;
    public String comment;
    public float score;

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public static QuestionReview create(HomeworkSubmitAnswer answer) {
        QuestionReview review = new QuestionReview();
        review.setComment(answer.comment);
        review.setCorrect(answer.correct);
        review.setScore(answer.score);
        return review;
    }

    public String getScore() {
        return String.valueOf(score);
    }
}
