package com.onyx.android.sdk.data.model.homework;

import com.onyx.android.sdk.data.model.Subject;
import com.onyx.android.sdk.utils.DateTimeUtil;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/11/24.
 */

public class Homework {

    public String _id;
    public String title;
    public Map<String, Integer> difficultyCount;
    public Map<String, Integer> quesTypeCount;
    public List<Question> questions;
    public Date beginTime;
    public Date endTime;
    public Subject subject;
    public boolean published;
    public boolean hasReview;
    public boolean publishedAnswer;

    public Homework() {
    }

    public Homework(String _id) {
        this._id = _id;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setHomeworkId(String id) {
        this._id = id;
    }

    public boolean isExpired() {
        return endTime != null && DateTimeUtil.convertGMTDateToLocal(endTime).before(Calendar.getInstance().getTime());
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean isPublishedAnswer() {
        return publishedAnswer;
    }

    public boolean hasReview() {
        return hasReview;
    }

    public void setPublishedAnswer(boolean publishedAnswer) {
        this.publishedAnswer = publishedAnswer;
    }

    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean needUpdateEndTime(Date time) {
        return time != null && (endTime == null || time.after(endTime));
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
