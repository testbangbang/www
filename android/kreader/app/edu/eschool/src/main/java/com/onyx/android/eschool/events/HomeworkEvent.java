package com.onyx.android.eschool.events;

import com.onyx.android.sdk.data.model.v2.Homework;

/**
 * Created by suicheng on 2018/1/12.
 */

public class HomeworkEvent {
    public Homework homework;

    public HomeworkEvent(Homework homework) {
        this.homework = homework;
    }
}
