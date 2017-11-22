package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.GetCorrectedTaskBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;

import java.util.List;

/**
 * Created by li on 2017/10/25.
 */

public interface CorrectView {
    void setCorrectData(GetCorrectedTaskBean data);

    void setQuestionBeanList(List<QuestionViewBean> questionList);

    void clearAdapter();
}
