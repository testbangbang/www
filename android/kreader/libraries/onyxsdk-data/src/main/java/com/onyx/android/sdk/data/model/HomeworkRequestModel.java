package com.onyx.android.sdk.data.model;

import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/11/24.
 */

public class HomeworkRequestModel {

    public String _id;
    public String title;
    public Map<String, Integer> difficultyCount;
    public Map<String, Integer> quesTypeCount;
    public List<Question> questions;
    public boolean published;
}
