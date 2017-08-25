package com.onyx.android.dr.reader.base;

import com.onyx.android.dr.reader.data.ReadSummaryGoodSentenceReviewBean;
import com.onyx.android.dr.reader.data.ReadSummaryNewWordReviewBean;

import java.util.List;

/**
 * Created by hehai on 17-8-24.
 */

public interface ReadSummaryView {

    void setNewWordList(List<ReadSummaryNewWordReviewBean> newWordList);

    void setGoodSentenceList(List<ReadSummaryGoodSentenceReviewBean> goodSentenceList);
}
