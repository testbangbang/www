package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

public class CommentEntity extends BaseObservable {
    private String contents;
    private String creationTime;
    private String id;
    private String nickname;
    private String pin;
    private String referenceId;
    private float score;
    private String userClient;
    private String userHead;
    private String userHeadFullUrl;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getUserClient() {
        return userClient;
    }

    public void setUserClient(String userClient) {
        this.userClient = userClient;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserHeadFullUrl() {
        return userHeadFullUrl;
    }

    public void setUserHeadFullUrl(String userHeadFullUrl) {
        this.userHeadFullUrl = userHeadFullUrl;
    }
}