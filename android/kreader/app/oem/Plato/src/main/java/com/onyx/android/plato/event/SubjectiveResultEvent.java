package com.onyx.android.plato.event;

/**
 * Created by li on 2017/10/23.
 */

public class SubjectiveResultEvent {
    private String questionId;

    public SubjectiveResultEvent(String questionID) {
        this.questionId = questionID;
    }

    public String getQuestionId() {
        return questionId;
    }
}
