package com.onyx.edu.homework.data;

import com.onyx.edu.homework.R;

/**
 * Created by lxm on 2018/1/29.
 */

public enum ScoreItemType {

    QUESTION_NUMBER(R.string.question_number),
    QUESTION_TYPE(R.string.question_type),
    STATE(R.string.state),
    SCORE_VALUE(R.string.score_value),
    SCORED(R.string.scored);

    private int titleResId;

    ScoreItemType(int titleResId) {
        this.titleResId = titleResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public static ScoreItemType getValue(int type) {
        int size = ScoreItemType.values().length;
        if (type >= size) {
            return ScoreItemType.QUESTION_NUMBER;
        }
        return ScoreItemType.values()[type];
    }
}
