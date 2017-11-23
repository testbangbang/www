package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.CourseAdapter;
import com.onyx.android.plato.adapter.HomeworkFinishedAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.FinishContent;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.ReportListBean;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.FinishedBinding;
import com.onyx.android.plato.event.HomeworkFinishedEvent;
import com.onyx.android.plato.event.HomeworkReportEvent;
import com.onyx.android.plato.event.HomeworkUnfinishedEvent;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.utils.HomeworkFinishComparator;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.PageRecyclerView;
import com.onyx.android.plato.view.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * Created by li on 2017/11/22.
 */

public class FinishedFragment extends BaseFragment implements View.OnClickListener, TimePickerDialog.TimePickerDialogInterface, HomeworkView {
    private FinishedBinding finishedBinding;
    private CourseAdapter courseAdapter;
    private CourseAdapter courseStateAdapter;
    private TimePickerDialog timePickerDialog;
    private String startTime;
    private String endTime;
    private HomeworkPresenter homeworkPresenter;
    private int subjectId;
    private String subjectType;
    private List<SubjectBean> subjects;
    private List<SubjectBean> exerciseTypes;
    private HomeworkFinishedAdapter homeworkFinishedAdapter;

    @Override
    protected void loadData() {
        homeworkPresenter = new HomeworkPresenter(this);
        //TODO:fake student id = 106
        homeworkPresenter.getSubjects("106");
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        finishedBinding = (FinishedBinding) binding;
        finishedBinding.finishedRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        finishedBinding.finishedRecyclerView.addItemDecoration(dividerItemDecoration);
        homeworkFinishedAdapter = new HomeworkFinishedAdapter();
        finishedBinding.finishedRecyclerView.setAdapter(homeworkFinishedAdapter);

        finishedBinding.finishedCourseRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        dividerItemDecoration.setSpace(10);
        finishedBinding.finishedCourseRecyclerView.addItemDecoration(dividerItemDecoration);
        courseAdapter = new CourseAdapter();
        finishedBinding.finishedCourseRecyclerView.setAdapter(courseAdapter);

        finishedBinding.finishedStateRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        finishedBinding.finishedStateRecyclerView.addItemDecoration(dividerItemDecoration);
        courseStateAdapter = new CourseAdapter();
        finishedBinding.finishedStateRecyclerView.setAdapter(courseStateAdapter);

        finishedBinding.setListener(this);
        timePickerDialog = new TimePickerDialog(getActivity());
        timePickerDialog.setDialogInterface(this);

        finishedBinding.finishedTitle.homeworkFinished.setSelected(true);
        finishedBinding.finishedTitle.homeworkUnfinished.setOnClickListener(this);
        finishedBinding.finishedTitle.homeworkFinished.setOnClickListener(this);
        finishedBinding.finishedTitle.homeworkStudyReport.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        courseAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                SubjectBean subjectBean = subjects.get((int) position);
                subjectId = subjectBean.id;
                loadHomeworkFinish();
            }
        });

        courseStateAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                SubjectBean subjectBean = exerciseTypes.get((int) position);
                subjectType = subjectBean.type;
                loadHomeworkFinish();
            }
        });
    }

    @Override
    protected int getRootView() {
        return R.layout.finished_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finished_start_time:
                showDialog(R.id.finished_start_time);
                break;
            case R.id.finished_end_time:
                showDialog(R.id.finished_end_time);
                break;
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

    private void showDialog(int id) {
        timePickerDialog.show();
        timePickerDialog.setId(id);
    }

    @Override
    public void positiveListener() {
        String dateTime = timePickerDialog.getDateTime();
        switch (timePickerDialog.getId()) {
            case R.id.finished_start_time:
                finishedBinding.finishedStartTime.setText(dateTime);
                this.startTime = dateTime;
                loadHomeworkFinish();
                break;
            case R.id.finished_end_time:
                finishedBinding.finishedEndTime.setText(dateTime);
                this.endTime = dateTime;
                loadHomeworkFinish();
                break;
        }
    }

    private void loadHomeworkFinish() {
        homeworkPresenter.getHomeworkFinishedData(SunApplication.getStudentId() + "", subjectId + "", startTime, endTime, subjectType);
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {

    }

    @Override
    public void setFinishedData(List<FinishContent> content) {
        if (homeworkFinishedAdapter != null) {
            Collections.sort(content, new HomeworkFinishComparator());
            homeworkFinishedAdapter.setData(content);
        }
    }

    @Override
    public void setReportData(List<ReportListBean> content) {

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
        this.exerciseTypes = exerciseTypes;
        subjectType = exerciseTypes.get(0).type;
        courseStateAdapter.setData(exerciseTypes);
    }

    @Override
    public void setNullFinishedData() {
        if (homeworkFinishedAdapter != null) {
            homeworkFinishedAdapter.clear();
        }
    }
}
