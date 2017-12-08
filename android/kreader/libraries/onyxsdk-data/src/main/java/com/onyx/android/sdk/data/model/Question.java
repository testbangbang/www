package com.onyx.android.sdk.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lxm on 2017/11/24.
 */

public class Question implements Serializable {

    public String _id;
    public String content;
    public String answers;
    public int QuesType;
    public int difficulty;
    public List<QuestionOption> options;

    public QuestionType getType() {
        int index= QuesType - 1;
        if (index >= QuestionType.values().length) {
            return QuestionType.SINGLE;
        }
        return QuestionType.values()[index];
    }

    public boolean isChoiceQuestion() {
        return getType() == QuestionType.SINGLE || getType() == QuestionType.MULTIPLE;
    }

    public boolean isSingleChoiceQuestion() {
        return getType() == QuestionType.SINGLE;
    }
}
