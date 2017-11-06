package com.onyx.android.sun.cloud.bean;

/**
 * Created by li on 2017/11/4.
 */

public class QuestionViewBean {
    private String showType;
    private int exeNumber;
    private int allScore;
    private ExerciseBean exerciseBean;
    private boolean isShow;

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

    public ExerciseBean getExerciseBean() {
        return exerciseBean;
    }

    public void setExerciseBean(ExerciseBean exerciseBean) {
        this.exerciseBean = exerciseBean;
    }
}
