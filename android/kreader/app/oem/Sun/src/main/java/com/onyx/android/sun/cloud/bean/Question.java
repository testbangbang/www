package com.onyx.android.sun.cloud.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/10/10.
 */

public class Question {
    public String question;
    public String type;
    public int id;
    public List<Map<String, String>> selection;
    public String userAnswer = "";
}
