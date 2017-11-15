package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by li on 2017/11/3.
 */

public class AnswerBean {
    public int exerciseId;
    public int state;
    public String answer;
    public boolean isCorrect;
    public int score;
    public int value;
    public List<KnowledgeBean> knowledgeDtoList;
    public double accuracy;
}
