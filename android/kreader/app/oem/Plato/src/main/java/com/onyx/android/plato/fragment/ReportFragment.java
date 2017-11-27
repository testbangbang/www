package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.CourseAdapter;
import com.onyx.android.plato.adapter.StudyReportAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.ReportListBean;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.ReportBinding;
import com.onyx.android.plato.event.HomeworkFinishedEvent;
import com.onyx.android.plato.event.HomeworkReportEvent;
import com.onyx.android.plato.event.HomeworkUnfinishedEvent;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/11/22.
 */

public class ReportFragment extends BaseFragment implements HomeworkView, View.OnClickListener {
    private ReportBinding reportBinding;
    private StudyReportAdapter studyReportAdapter;
    private CourseAdapter courseAdapter;
    private HomeworkPresenter homeworkPresenter;
    private List<SubjectBean> subjects;
    private int subjectId;

    @Override
    protected void loadData() {
        homeworkPresenter = new HomeworkPresenter(this);
        //TODO:fake student id = 106
        homeworkPresenter.getSubjects(SunApplication.getStudentId() + "");
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        reportBinding = (ReportBinding) binding;
        reportBinding.reportRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        reportBinding.reportRecyclerView.addItemDecoration(dividerItemDecoration);
        studyReportAdapter = new StudyReportAdapter();
        reportBinding.reportRecyclerView.setAdapter(studyReportAdapter);

        reportBinding.reportCourseRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        dividerItemDecoration.setSpace(10);
        reportBinding.reportCourseRecyclerView.addItemDecoration(dividerItemDecoration);
        courseAdapter = new CourseAdapter();
        reportBinding.reportCourseRecyclerView.setAdapter(courseAdapter);

        reportBinding.reportTitle.homeworkStudyReport.setSelected(true);
        reportBinding.reportTitle.homeworkUnfinished.setOnClickListener(this);
        reportBinding.reportTitle.homeworkFinished.setOnClickListener(this);
        reportBinding.reportTitle.homeworkStudyReport.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        courseAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                SubjectBean subjectBean = subjects.get((int) position);
                subjectId = subjectBean.id;
                homeworkPresenter.getStudyReportData(subjectId, SunApplication.getStudentId());
            }
        });
    }

    @Override
    protected int getRootView() {
        return R.layout.report_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {

    }

    @Override
    public void setFinishedData(List<ContentBean> content) {

    }

    @Override
    public void setReportData(List<ReportListBean> content) {
        if (studyReportAdapter != null) {
            studyReportAdapter.setData(content);
        }
    }

    @Override
    public void setTaskDetail(QuestionDetail data) {

    }

    @Override
    public void setAnswerRecord(List<TaskAndAnswerEntity> taskList) {

    }

    @Override
    public void setStudyReportDetail(StudyReportDetailBean data) {

    }

    @Override
    public void setSubjects(List<SubjectBean> subjects) {
        this.subjects = subjects;
        subjectId = subjects.get(0).id;
        courseAdapter.setData(subjects);
    }

    @Override
    public void setExerciseType(List<SubjectBean> exerciseTypes) {

    }

    @Override
    public void setNullFinishedData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homework_unfinished:
                EventBus.getDefault().post(new HomeworkUnfinishedEvent());
                break;
            case R.id.homework_finished:
                EventBus.getDefault().post(new HomeworkFinishedEvent());
                break;
            case R.id.homework_study_report:
                EventBus.getDefault().post(new HomeworkReportEvent());
                break;
        }
    }
}
