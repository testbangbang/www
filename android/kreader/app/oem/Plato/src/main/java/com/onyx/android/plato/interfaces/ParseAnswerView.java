package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.AnalysisBean;
import com.onyx.android.plato.cloud.bean.PracticeParseBean;

/**
 * Created by li on 2017/10/26.
 */

public interface ParseAnswerView {
    void setExplanation(PracticeParseBean data);

    void setRecordDuration(long recordDuration);

    void setAnalysis(AnalysisBean analysisBean);

    void setVoiceUrl(String voiceUrl);

    void insertAnalysis();
}
