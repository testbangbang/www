package com.onyx.android.sun.event;

import com.onyx.android.sun.cloud.bean.Question;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerEvent {
    private String title;
    private Question question;

    public ParseAnswerEvent(Question question, String title) {
        this.question = question;
        this.title = title;
    }

    public Question getQuestion() {
        return question;
    }

    public String getTitle() {
        return title;
    }
}
