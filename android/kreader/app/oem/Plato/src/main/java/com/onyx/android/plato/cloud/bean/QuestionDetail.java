package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by li on 2017/10/10.
 */

public class QuestionDetail {
    public List<Integer> ids;
    public String name;
    public int volumeScore;
    public long duration;
    public int volumeType;
    public List<QuestionData> volumeExerciseDTOS;
    public int taskId;
}
