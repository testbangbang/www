package com.onyx.android.plato.event;

import com.onyx.android.plato.cloud.bean.QuestionViewBean;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerEvent {
    private String title;
    private QuestionViewBean questionViewBean;

    public ParseAnswerEvent(QuestionViewBean questionViewBean, String title) {
        this.title = title;
        this.questionViewBean = questionViewBean;
    }

    public QuestionViewBean getQuestion() {
        return questionViewBean;
    }

    public String getTitle() {
        return title;
    }
}
