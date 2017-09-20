package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.reader.data.ReadSummaryEntity;

import java.util.List;

/**
 * Created by hehai on 17-9-20.
 */

public interface SummaryView {
    void setSummaryList(List<ReadSummaryEntity> summaryList);
}
