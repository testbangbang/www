package com.onyx.android.sun.interfaces;

import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.QuestionDetail;

import java.util.List;

/**
 * Created by li on 2017/10/11.
 */

public interface HomeworkView {
    void setUnfinishedData(List<ContentBean> content);

    void setFinishedData(List<FinishContent> content);

    void setReportData(List<FinishContent> content);

    void setTaskDetail(QuestionDetail data);
}
