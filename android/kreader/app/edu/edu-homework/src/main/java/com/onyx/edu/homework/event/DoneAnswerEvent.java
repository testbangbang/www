package com.onyx.edu.homework.event;

import com.onyx.android.sdk.data.model.Question;

/**
 * Created by lxm on 2017/12/9.
 */

public class DoneAnswerEvent {

    public Question question;

    public DoneAnswerEvent(Question question) {
        this.question = question;
    }
}
