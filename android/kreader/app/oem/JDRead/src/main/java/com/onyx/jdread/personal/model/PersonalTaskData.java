package com.onyx.jdread.personal.model;

import android.databinding.BaseObservable;

/**
 * Created by li on 2018/1/30.
 */

public class PersonalTaskData extends BaseObservable {
    private String taskName;
    private String taskStatus;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        notifyChange();
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
        notifyChange();
    }
}
