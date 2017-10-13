package com.onyx.android.sun.cloud.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/10/10.
 */

public class QuestionDetail {
    public int code;
    public String type;
    public String course;
    public String title;
    public String deadline;
    public List<QuestionData> data;
    public int totalPoints;
    public long duration;
}
