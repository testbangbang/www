package com.onyx.android.edu.bean;

import java.util.List;

/**
 * Created by ming on 16/6/28.
 * 试卷结果类
 */
public class PaperResult {

    public List<Boolean> result;

    public Float score;

    public List<Boolean> getResult() {
        return result;
    }

    public void setResult(List<Boolean> result) {
        this.result = result;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
