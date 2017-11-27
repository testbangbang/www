package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.HomeworkUnfinishedAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.ReportListBean;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.UnfinishedBinding;
import com.onyx.android.plato.event.HomeworkFinishedEvent;
import com.onyx.android.plato.event.HomeworkReportEvent;
import com.onyx.android.plato.event.HomeworkUnfinishedEvent;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * Created by li on 2017/11/22.
 */

public class UnfinishedFragment extends BaseFragment implements HomeworkView, View.OnClickListener {
    private UnfinishedBinding unfinishedBinding;
    private HomeworkUnfinishedAdapter homeworkUnfinishedAdapter;

    @Override
    protected void loadData() {
        HomeworkPresenter homeworkPresenter = new HomeworkPresenter(this);
        homeworkPresenter.getHomeworkUnfinishedData(SunApplication.getStudentId() + "");
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        unfinishedBinding = (UnfinishedBinding)binding;
        unfinishedBinding.unfinishedRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        unfinishedBinding.unfinishedRecyclerView.addItemDecoration(dividerItemDecoration);
        homeworkUnfinishedAdapter = new HomeworkUnfinishedAdapter();
        unfinishedBinding.unfinishedRecyclerView.setAdapter(homeworkUnfinishedAdapter);
        unfinishedBinding.unfinishedTitle.homeworkUnfinished.setSelected(true);
        unfinishedBinding.unfinishedTitle.homeworkUnfinished.setOnClickListener(this);
        unfinishedBinding.unfinishedTitle.homeworkFinished.setOnClickListener(this);
        unfinishedBinding.unfinishedTitle.homeworkStudyReport.setOnClickListener(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.un_finished_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {
        if (homeworkUnfinishedAdapter != null) {
            Collections.sort(content);
            homeworkUnfinishedAdapter.setData(content);
        }
    }

    @Override
    public void setFinishedData(List<ContentBean> content) {

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
