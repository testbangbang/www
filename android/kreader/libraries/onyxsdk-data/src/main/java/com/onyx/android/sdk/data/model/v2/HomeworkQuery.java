package com.onyx.android.sdk.data.model.v2;

/**
 * Created by suicheng on 2017/12/25.
 */
public class HomeworkQuery extends ResourceQuery {

    private String subject;

    public HomeworkQuery(int code, String ref) {
        super(code, ref);
    }

    public String getSubject() {
        return subject;
    }

    public HomeworkQuery setSubject(String subject) {
        this.subject = subject;
        return this;
    }
}
