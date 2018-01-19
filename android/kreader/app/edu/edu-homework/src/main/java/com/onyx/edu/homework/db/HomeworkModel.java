package com.onyx.edu.homework.db;

import com.onyx.android.sdk.data.model.Subject;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.sql.Date;

/**
 * Created by lxm on 2017/12/11.
 */

@Table(database = HomeworkDatabase.class)
public class HomeworkModel extends BaseModel {

    @PrimaryKey
    @Column
    @Unique
    private String uniqueId;

    // 0 doing 1 done 2 review
    @Column
    private int state;

    @Column
    private Date beginTime;

    @Column
    private Date endTime;

    @Column(typeConverter = ConverterSubject.class)
    private Subject subject;

    @Column
    private String title;

    @Column
    String extraAttributes;

    @Column
    boolean hasReview;

    @Column
    boolean publishedAnswer;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(String extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    public boolean isHasReview() {
        return hasReview;
    }

    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }

    public boolean isPublishedAnswer() {
        return publishedAnswer;
    }

    public void setPublishedAnswer(boolean publishedAnswer) {
        this.publishedAnswer = publishedAnswer;
    }

    public static HomeworkModel create(String uniqueId) {
        HomeworkModel model = new HomeworkModel();
        model.setUniqueId(uniqueId);
        return model;
    }

    public void loadFromHomeworkRequestModel(Homework requestModel) {
        if (requestModel == null) {
            return;
        }
        setBeginTime(requestModel.beginTime);
        setEndTime(requestModel.endTime);
        setSubject(requestModel.subject);
        setTitle(requestModel.title);
    }

    public void loadFromHomework(Homework homework) {
        if (homework == null) {
            return;
        }
        setBeginTime(homework.beginTime);
        setEndTime(homework.endTime);
        setHasReview(homework.hasReview);
        setPublishedAnswer(homework.publishedAnswer);
    }

}
