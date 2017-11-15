package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by li on 2017/10/10.
 */

public class Question {
    public int id;
    public String content;
    public List<ExerciseSelectionBean> exerciseSelections;
    public String userAnswer;
}
