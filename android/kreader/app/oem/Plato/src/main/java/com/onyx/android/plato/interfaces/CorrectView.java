package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.QuestionData;

import java.util.List;

/**
 * Created by li on 2017/10/25.
 */

public interface CorrectView {
    void setCorrectList(List<QuestionData> data);
}
