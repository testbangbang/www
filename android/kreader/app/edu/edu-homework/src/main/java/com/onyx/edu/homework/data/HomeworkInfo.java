package com.onyx.edu.homework.data;

import com.onyx.android.sdk.data.model.HomeworkRequestModel;

import java.util.Date;

/**
 * Created by lxm on 2017/12/22.
 */

public class HomeworkInfo {

    public String homeworkId;
    public String title;
    public Date beginTime;
    public Date endTime;
    public String subject;

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void loadFromHomeworkRequestModel(HomeworkRequestModel homeworkRequestModel) {
        if (homeworkRequestModel == null) {
            return;
        }
        setBeginTime(homeworkRequestModel.beginTime);
        setEndTime(homeworkRequestModel.endTime);
        setSubject(homeworkRequestModel.subject);
    }
}
