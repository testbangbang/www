package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.CourseAdapter;
import com.onyx.android.plato.adapter.HomeworkFinishedAdapter;
import com.onyx.android.plato.adapter.HomeworkUnfinishedAdapter;
import com.onyx.android.plato.adapter.StudyReportAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.FinishContent;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.databinding.HomeworkBinding;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.utils.HomeworkFinishComparator;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.PageRecyclerView;
import com.onyx.android.plato.view.TimePickerDialog;

import java.util.Collections;
import java.util.List;

/**
 * Created by li on 2017/9/30.
 */

public class HomeWorkFragment extends BaseFragment implements View.OnClickListener, TimePickerDialog.TimePickerDialogInterface, HomeworkView {
    private HomeworkBinding homeworkBinding;
    private String[] course = new String[]{SunApplication.getInstance().getResources().getString(R.string.homework_course_Chinese),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_English),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_politic),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_history),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_creature),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_mathematics)};

    private String[] courseState = new String[]{SunApplication.getInstance().getResources().getString(R.string.homework_course_all),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_exercise),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_test_paper),
            SunApplication.getInstance().getResources().getString(R.string.homework_course_competition)};
    private TimePickerDialog timePickerDialog;
    private HomeworkPresenter homeworkPresenter;
    private HomeworkUnfinishedAdapter homeworkUnfinishedAdapter;
    private HomeworkFinishedAdapter homeworkFinishedAdapter;
    private StudyReportAdapter studyReportAdapter;
    private DividerItemDecoration dividerItemDecoration;
    private CourseAdapter courseAdapter;
    private CourseAdapter courseStateAdapter;
    private List<SubjectBean> subjects;
    private List<SubjectBean> exerciseTypes;
    private int subjectId;
    private String subjectType;
    private String startTime;
    private String endTime;

    @Override
    protected void loadData() {
        homeworkPresenter = new HomeworkPresenter(this);
        //TODO:fake student id = 108
        homeworkPresenter.getSubjects("107");
        homeworkPresenter.getHomeworkUnfinishedData("1");
        homeworkPresenter.getStudyReportData(null);
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        homeworkBinding = (HomeworkBinding) binding;
        homeworkBinding.homeworkRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        homeworkBinding.homeworkRecyclerView.addItemDecoration(dividerItemDecoration);
        homeworkUnfinishedAdapter = new HomeworkUnfinishedAdapter();
        homeworkBinding.homeworkRecyclerView.setAdapter(homeworkUnfinishedAdapter);

        homeworkBinding.homeworkCourseRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        dividerItemDecoration.setSpace(10);
        homeworkBinding.homeworkCourseRecyclerView.addItemDecoration(dividerItemDecoration);
        courseAdapter = new CourseAdapter();
        homeworkBinding.homeworkCourseRecyclerView.setAdapter(courseAdapter);

        homeworkBinding.homeworkStateRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        homeworkBinding.homeworkStateRecyclerView.addItemDecoration(dividerItemDecoration);
        courseStateAdapter = new CourseAdapter();
        homeworkBinding.homeworkStateRecyclerView.setAdapter(courseStateAdapter);

        homeworkFinishedAdapter = new HomeworkFinishedAdapter();
        studyReportAdapter = new StudyReportAdapter();
        homeworkBinding.setListener(this);
        setSelected(R.id.homework_unfinished);
        timePickerDialog = new TimePickerDialog(getActivity());
        timePickerDialog.setDialogInterface(this);
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
                subjectType = subjectBean.name;
                loadHomeworkFinish();
            }
        });
    }

    private void loadHomeworkFinish() {
        //TODO:fake student id
        homeworkPresenter.getHomeworkFinishedData("107",subjectId+"", startTime, endTime, subjectType);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected int getRootView() {
        return R.layout.homework_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homework_finished:
                finished();
                break;
            case R.id.homework_unfinished:
                unfinished();
                break;
            case R.id.homework_study_report:
                studyReport();
                break;
            case R.id.homework_start_time:
                showDialog(R.id.homework_start_time);
                break;
            case R.id.homework_end_time:
                showDialog(R.id.homework_end_time);
                break;
        }
    }

    private void showDialog(int id) {
        timePickerDialog.show();
        timePickerDialog.setId(id);
    }

    private void studyReport() {
        setSelected(R.id.homework_study_report);
        homeworkBinding.homeworkRecyclerView.setAdapter(studyReportAdapter);
    }

    private void unfinished() {
        setSelected(R.id.homework_unfinished);
        homeworkBinding.homeworkRecyclerView.setAdapter(homeworkUnfinishedAdapter);
    }

    private void finished() {
        setSelected(R.id.homework_finished);
        homeworkBinding.homeworkRecyclerView.setAdapter(homeworkFinishedAdapter);
    }

    private void setSelected(int id) {
        homeworkBinding.homeworkFinished.setSelected(R.id.homework_finished == id);
        homeworkBinding.homeworkUnfinished.setSelected(R.id.homework_unfinished == id);
        homeworkBinding.homeworkStudyReport.setSelected(R.id.homework_study_report == id);

        homeworkBinding.setIsVisible(R.id.homework_finished == id);
        homeworkBinding.setShowCourse(R.id.homework_finished == id || R.id.homework_study_report == id);
        dividerItemDecoration.setDrawLine(R.id.homework_finished != id);
    }

    @Override
    public void positiveListener() {
        String dateTime = timePickerDialog.getDateTime();
        switch (timePickerDialog.getId()) {
            case R.id.homework_start_time:
                homeworkBinding.homeworkStartTime.setText(dateTime);
                this.startTime = dateTime;
                loadHomeworkFinish();
                break;
            case R.id.homework_end_time:
                homeworkBinding.homeworkEndTime.setText(dateTime);
                this.endTime = dateTime;
                loadHomeworkFinish();
                break;
        }
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {
        if (homeworkUnfinishedAdapter != null) {
            Collections.sort(content);
            homeworkUnfinishedAdapter.setData(content);
        }
    }

    @Override
    public void setFinishedData(List<FinishContent> content) {
        if (homeworkFinishedAdapter != null) {
            Collections.sort(content, new HomeworkFinishComparator());
            homeworkFinishedAdapter.setData(content);
        }
    }

    @Override
    public void setReportData(List<FinishContent> content) {
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
        this.exerciseTypes = exerciseTypes;
        subjectType = exerciseTypes.get(0).name;
        courseStateAdapter.setData(exerciseTypes);
    }
}
