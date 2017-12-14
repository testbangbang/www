package com.onyx.edu.homework.event;

/**
 * Created by lxm on 2017/12/14.
 */

public class GotoQuestionPageEvent {

    public int page;
    public boolean hideRecord;

    public GotoQuestionPageEvent(int page, boolean hideRecord) {
        this.page = page;
        this.hideRecord = hideRecord;
    }
}
