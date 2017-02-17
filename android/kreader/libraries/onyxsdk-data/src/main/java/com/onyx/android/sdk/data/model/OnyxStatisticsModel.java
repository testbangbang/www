package com.onyx.android.sdk.data.model;


import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.converter.SetStringConverter;
import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ming on 2017/2/7.
 */
@Table(database = OnyxStatisticsDatabase.class)
public class OnyxStatisticsModel extends BaseStatisticsModel {

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
        return durationTime;
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
}
