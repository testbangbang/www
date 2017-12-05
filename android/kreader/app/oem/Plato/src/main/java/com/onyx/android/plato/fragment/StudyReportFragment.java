package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.ReportListBean;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.FragmentStudyReportBinding;
import com.onyx.android.plato.event.OnBackPressEvent;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.view.TableView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class StudyReportFragment extends BaseFragment implements HomeworkView, View.OnClickListener {
    private HomeworkPresenter homeworkPresenter;
    private FragmentStudyReportBinding studyReportBinding;
    String[] heads = SunApplication.getInstance().getResources().getStringArray(R.array.study_report_table_heads);
    private String title = "";
    private int id;

    @Override
    protected void loadData() {
        if (homeworkPresenter == null) {
            homeworkPresenter = new HomeworkPresenter(this);
        }
        homeworkPresenter.getStudyReportDetail(id);
    }

    private void setSpiderWebViewScoresData(List<StudyReportDetailBean.CompetenceBean> competenceDatas) {
        float[] scoresClass = new float[competenceDatas.size()];
        float[] scoresOwn = new float[competenceDatas.size()];

        for (int i = 0; i < competenceDatas.size(); i++) {
            StudyReportDetailBean.CompetenceBean competenceBean = competenceDatas.get(i);
            scoresClass[i] = competenceBean.points.classX;
            scoresOwn[i] = competenceBean.points.own;

            TextView nameTextView = new TextView(getActivity());
            nameTextView.setText(competenceBean.name);
            studyReportBinding.circularLayout.addView(nameTextView);
        }

        studyReportBinding.spiderWebScoreView.setScores(10f,scoresOwn,scoresClass);

    }

    private void setTableData(List<StudyReportDetailBean.DataBean> dataList, String[] heads) {
        ArrayList<Integer> rowTypes = new ArrayList();
        TableView tableView = studyReportBinding.tableView.clearTableContents().setHeader(heads);

        for (int i = 0; i < dataList.size(); i++) {
            StudyReportDetailBean.DataBean dataBean = dataList.get(i);
            List<StudyReportDetailBean.DataBean.MapBean> map = dataBean.map;

            for (int j = 0; j < map.size(); j++) {
                if (j>0){
                    rowTypes.add(0);
                }else {
                    rowTypes.add(map.size());
                }
                StudyReportDetailBean.DataBean.MapBean mapBean = map.get(j);
                tableView.addContent(dataBean.KN,mapBean.NO,String.valueOf(mapBean.points),String.valueOf(mapBean.score),String.valueOf(mapBean.avg),dataBean.process * 100 +getString(R.string.study_progress));
            }
        }

        tableView.setRowTypes(rowTypes);
        tableView.refreshTable();
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        studyReportBinding = (FragmentStudyReportBinding) binding;
        homeworkPresenter = new HomeworkPresenter(this);
        studyReportBinding.setListener(this);
        studyReportBinding.setStudyReportTitle(title);
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
            case R.id.tv_study_report_title:
                onKeyBack();
                break;
        }
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {

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
        if (data != null){
            setSpiderWebViewScoresData(data.competence);
            setTableData(data.data,heads);
            studyReportBinding.setStudyReportDetail(data);
        }
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

    public void setPracticeId(int id,String title) {
        this.title = title;
        this.id = id;
        if (homeworkPresenter != null){
            homeworkPresenter.getStudyReportDetail(id);
        }
    }
}