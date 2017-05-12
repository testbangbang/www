package com.onyx.android.sdk.data.model;


import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.converter.SetStringConverter;
import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ming on 2017/2/7.
 */
@Table(database = OnyxStatisticsDatabase.class)
public class OnyxStatisticsModel extends BaseStatisticsModel {

    private static final long MAX_PAGE_DURATION_TIME = 10 * 60 * 1000;

    @Column
    private String orgText;
    @Column
    private String note;
    @Column
    private String path;
    @Column
    private String title;
    @Column
    private String name;
    @Column(typeConverter = ListStringConverter.class)
    private List<String> author;
    @Column
    private Integer lastPage;
    @Column
    private Integer currPage;
    @Column
    private Long durationTime;
    @Column
    private String comment;
    @Column
    private Integer score;

    public OnyxStatisticsModel() {
    }

    public OnyxStatisticsModel(String md5, String md5short, String sid, Integer type, Date eventTime) {
        super(md5, md5short, sid, type, eventTime);
    }

    public OnyxStatisticsModel(String md5, String md5short, String sid, Integer type, Date eventTime, int status) {
        super(md5, md5short, sid, type, eventTime, status);
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public Integer getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Long getDurationTime() {
        return durationTime > MAX_PAGE_DURATION_TIME ? MAX_PAGE_DURATION_TIME : durationTime;
    }

    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrgText() {
        return orgText;
    }

    public void setOrgText(String orgText) {
        this.orgText =  orgText;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public static OnyxStatisticsModel create(String md5, String md5short, String sid, Integer type, Date eventTime) {
        return new OnyxStatisticsModel(md5, md5short, sid, type, eventTime);
    }

    public static OnyxStatisticsModel create(String md5, String md5short, String sid, Integer type, Date eventTime, int status) {
        return new OnyxStatisticsModel(md5, md5short, sid, type, eventTime, status);
    }
}
