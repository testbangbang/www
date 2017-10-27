package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.QuestionDetail;
import com.onyx.android.sun.cloud.bean.StudyReportDetailBean;
import com.onyx.android.sun.databinding.FragmentStudyReportBinding;
import com.onyx.android.sun.event.OnBackPressEvent;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.presenter.HomeworkPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class StudyReportFragment extends BaseFragment implements HomeworkView, View.OnClickListener {

    private HomeworkPresenter homeworkPresenter;
    private FragmentStudyReportBinding studyReportBinding;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        studyReportBinding = (FragmentStudyReportBinding) binding;
        homeworkPresenter = new HomeworkPresenter(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_study_report;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new OnBackPressEvent(ChildViewID.FRAGMENT_STUDY_REPORT));
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_study_report_look_competence:
                break;
        }
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {

    }

    @Override
    public void setFinishedData(List<FinishContent> content) {

    }

    @Override
    public void setReportData(List<FinishContent> content) {

    }

    @Override
    public void setTaskDetail(QuestionDetail data) {

    }

    @Override
    public void setStudyReportDetail(StudyReportDetailBean data) {

    }

    public void setPracticeId(int id) {
        if (homeworkPresenter != null){
            homeworkPresenter.getStudyReportDetail(id);
        }
    }
}
