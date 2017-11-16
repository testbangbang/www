package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.FinishContent;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;

import java.util.List;

/**
 * Created by li on 2017/10/11.
 */

public interface HomeworkView {
    void setUnfinishedData(List<ContentBean> content);

    void setFinishedData(List<FinishContent> content);

    void setReportData(List<FinishContent> content);

    void setTaskDetail(QuestionDetail data);

    void setAnswerRecord(List<TaskAndAnswerEntity> taskList);

    void setStudyReportDetail(StudyReportDetailBean data);

    void setSubjects(List<SubjectBean> subjects);

    void setExerciseType(List<SubjectBean> exerciseTypes);
}
