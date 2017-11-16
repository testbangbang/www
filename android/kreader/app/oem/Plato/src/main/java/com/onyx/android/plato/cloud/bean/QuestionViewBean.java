package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by li on 2017/11/4.
 */

public class QuestionViewBean {
    private String showType;
    private int exeNumber;
    private int allScore;
    private boolean isShow;

    private int parentId;
    private String scene;

    private int id;
    private String content;
    private List<ExerciseSelectionBean> exerciseSelections;
    private String userAnswer;
    private boolean isShowReaderComprehension;

    public boolean isShowReaderComprehension() {
        return isShowReaderComprehension;
    }

    public void setShowReaderComprehension(boolean showReaderComprehension) {
        isShowReaderComprehension = showReaderComprehension;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ExerciseSelectionBean> getExerciseSelections() {
        return exerciseSelections;
    }

    public void setExerciseSelections(List<ExerciseSelectionBean> exerciseSelections) {
        this.exerciseSelections = exerciseSelections;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public int getExeNumber() {
        return exeNumber;
    }

    public void setExeNumber(int exeNumber) {
        this.exeNumber = exeNumber;
    }

    public int getAllScore() {
        return allScore;
    }

    public void setAllScore(int allScore) {
        this.allScore = allScore;
    }
}
