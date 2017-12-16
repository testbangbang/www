package com.onyx.android.sdk.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lxm on 2017/12/13.
 */

public class QuestionReview implements Serializable {

    // 0 not review 1 right 2 wrong
    public int correct;
    public String comment;
    public float score;
    public List<String> attachment;
    public List<String> attachmentUrl;

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public boolean isRightAnswer() {
        return  correct == 1;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public void setAttachmentUrl(List<String> attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public static QuestionReview create(HomeworkSubmitAnswer answer) {
        QuestionReview review = new QuestionReview();
        review.setComment(answer.comment);
        review.setCorrect(answer.correct);
        review.setScore(answer.score);
        review.setAttachment(answer.attachment);
        review.setAttachmentUrl(answer.attachmentUrl);
        return review;
    }

    public String getScore() {
        return String.valueOf(score);
    }
}
