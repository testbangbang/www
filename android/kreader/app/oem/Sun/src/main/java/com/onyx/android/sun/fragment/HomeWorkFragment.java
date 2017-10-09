package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.adapter.CourseAdapter;
import com.onyx.android.sun.adapter.HomeworkAdapter;
import com.onyx.android.sun.databinding.HomeworkBinding;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;
import com.onyx.android.sun.view.TimePickerDialog;

/**
 * Created by li on 2017/9/30.
 */

public class HomeWorkFragment extends BaseFragment implements View.OnClickListener, TimePickerDialog.TimePickerDialogInterface {
    private HomeworkBinding homeworkBinding;
    private String[] course = new String[]{SunApplication.getInstence().getResources().getString(R.string.homework_course_Chinese),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_English),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_politic),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_history),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_creature),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_mathematics),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_mathematics)};

    private String[] courseState = new String[]{SunApplication.getInstence().getResources().getString(R.string.homework_course_all),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_exercise),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_test_paper),
            SunApplication.getInstence().getResources().getString(R.string.homework_course_competition)};
    private TimePickerDialog timePickerDialog;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        homeworkBinding = (HomeworkBinding)binding;
        homeworkBinding.homeworkRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstence()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstence(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        homeworkBinding.homeworkRecyclerView.addItemDecoration(dividerItemDecoration);
        HomeworkAdapter homeworkAdapter = new HomeworkAdapter();
        homeworkBinding.homeworkRecyclerView.setAdapter(homeworkAdapter);

        homeworkBinding.homeworkCourseRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstence()));
        dividerItemDecoration.setSpace(10);
        homeworkBinding.homeworkCourseRecyclerView.addItemDecoration(dividerItemDecoration);
        CourseAdapter courseAdapter = new CourseAdapter();
        courseAdapter.setData(course);
        homeworkBinding.homeworkCourseRecyclerView.setAdapter(courseAdapter);

        homeworkBinding.homeworkStateRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstence()));
        homeworkBinding.homeworkStateRecyclerView.addItemDecoration(dividerItemDecoration);
        CourseAdapter courseStateAdapter = new CourseAdapter();
        courseStateAdapter.setData(courseState);
        homeworkBinding.homeworkStateRecyclerView.setAdapter(courseStateAdapter);
        homeworkBinding.setListener(this);
        setSelected(R.id.homework_unfinished);
        timePickerDialog = new TimePickerDialog(getActivity());
        timePickerDialog.setDialogInterface(this);
    }

    @Override
    protected void initListener() {

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
                setSelected(R.id.homework_finished);
                break;
            case R.id.homework_unfinished:
                setSelected(R.id.homework_unfinished);
                break;
            case R.id.homework_study_report:
                setSelected(R.id.homework_study_report);
                break;
            case R.id.homework_start_time:
                timePickerDialog.show();
                timePickerDialog.setId(R.id.homework_start_time);
                break;
            case R.id.homework_end_time:
                timePickerDialog.show();
                timePickerDialog.setId(R.id.homework_end_time);
                break;
        }
    }

    private void setSelected(int id) {
        homeworkBinding.homeworkFinished.setSelected(R.id.homework_finished == id);
        homeworkBinding.homeworkUnfinished.setSelected(R.id.homework_unfinished == id);
        homeworkBinding.homeworkStudyReport.setSelected(R.id.homework_study_report == id);

        homeworkBinding.setIsVisible(R.id.homework_finished == id);
    }

    @Override
    public void positiveListener() {
        String dateTime = timePickerDialog.getDateTime();
        switch (timePickerDialog.getId()) {
            case R.id.homework_start_time:
                homeworkBinding.homeworkStartTime.setText(dateTime);
                break;
            case R.id.homework_end_time:
                homeworkBinding.homeworkEndTime.setText(dateTime);
                break;
        }
    }
}
