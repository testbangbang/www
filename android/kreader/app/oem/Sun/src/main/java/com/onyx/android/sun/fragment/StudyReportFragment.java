package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.QuestionDetail;
import com.onyx.android.sun.cloud.bean.StudyReportDetailBean;
import com.onyx.android.sun.databinding.FragmentStudyReportBinding;
import com.onyx.android.sun.event.OnBackPressEvent;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.presenter.HomeworkPresenter;
import com.onyx.android.sun.view.TableView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class StudyReportFragment extends BaseFragment implements HomeworkView, View.OnClickListener {

    private HomeworkPresenter homeworkPresenter;
    private FragmentStudyReportBinding studyReportBinding;
    String[] heads = new String[]{SunApplication.getInstance().getString(R.string.study_report_fragment_head_Knowledge_point), SunApplication.getInstance().getString(R.string.study_report_fragment_head_practice_number), SunApplication.getInstance().getString(R.string.study_report_fragment_head_total_points),SunApplication.getInstance().getString(R.string.study_report_fragment_head_personal_score),SunApplication.getInstance().getString(R.string.study_report_fragment_head_class_average),SunApplication.getInstance().getString(R.string.study_report_fragment_head_progress)};

    @Override
    protected void loadData() {

        StudyReportDetailBean.CompetenceBean[] competenceBeans = new StudyReportDetailBean.CompetenceBean[5];

        for (int i = 0; i < 5 ; i++) {
            StudyReportDetailBean.CompetenceBean competenceBean= new StudyReportDetailBean.CompetenceBean();
            competenceBean.name = "第"+ (i+1) + "项";
            StudyReportDetailBean.CompetenceBean.PointsBean pointBean = new StudyReportDetailBean.CompetenceBean.PointsBean();
            pointBean.classX = i+6;
            pointBean.own = i+2;
            competenceBean.points = pointBean ;
            competenceBeans[i] = competenceBean;
        }

        setSpiderWebViewScoresData(competenceBeans);

        ArrayList<StudyReportDetailBean.DataBean> dataList = new ArrayList<>();
        String[] kns= new String[]{"立体几何","函数","集合与常用逻辑用语","常用逻辑用语常用逻辑用语常用逻辑用语","平面向量","概率"};

        for (int i = 0; i < 6; i++) {
            StudyReportDetailBean.DataBean bean = new StudyReportDetailBean.DataBean();
            bean.KN = kns[i];
            bean.KNId = i + "";
            bean.process = i * 0.03 ;
            ArrayList<StudyReportDetailBean.DataBean.MapBean> mapBeanArrayList = new ArrayList<>();

            if (i == 0){
                StudyReportDetailBean.DataBean.MapBean mapBean1 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean1.NO = new Random().nextInt(20) + 1 + "";
                mapBean1.score =  new Random().nextInt(20) + 1 ;
                mapBean1.avg =  new Random().nextInt(20) + 1 ;
                mapBean1.points =  30 ;

                StudyReportDetailBean.DataBean.MapBean mapBean2 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean2.NO = new Random().nextInt(20) + 1 + "";
                mapBean2.score =  new Random().nextInt(20) + 1 ;
                mapBean2.avg =  new Random().nextInt(20) + 1 ;
                mapBean2.points =  30 ;

                mapBeanArrayList.add(mapBean1);
                mapBeanArrayList.add(mapBean2);
            }else if (i == 2){
                StudyReportDetailBean.DataBean.MapBean mapBean1 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean1.NO = new Random().nextInt(20) + 1 + "";
                mapBean1.score =  new Random().nextInt(20) + 1 ;
                mapBean1.avg =  new Random().nextInt(20) + 1 ;
                mapBean1.points =  30 ;

                StudyReportDetailBean.DataBean.MapBean mapBean2 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean2.NO = new Random().nextInt(20) + 1 + "";
                mapBean2.score =  new Random().nextInt(20) + 1 ;
                mapBean2.avg =  new Random().nextInt(20) + 1 ;
                mapBean2.points =  30 ;

                StudyReportDetailBean.DataBean.MapBean mapBean3 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean3.NO = new Random().nextInt(20) + 1 + "";
                mapBean3.score =  new Random().nextInt(20) + 1 ;
                mapBean3.avg =  new Random().nextInt(20) + 1 ;
                mapBean3.points =  30 ;

                mapBeanArrayList.add(mapBean1);
                mapBeanArrayList.add(mapBean2);
                mapBeanArrayList.add(mapBean3);
            }else {

                StudyReportDetailBean.DataBean.MapBean mapBean1 = new StudyReportDetailBean.DataBean.MapBean();
                mapBean1.NO = new Random().nextInt(20) + 1 + "";
                mapBean1.score =  new Random().nextInt(20) + 1 ;
                mapBean1.avg =  new Random().nextInt(20) + 1 ;
                mapBean1.points =  30 ;
                mapBeanArrayList.add(mapBean1);
            }

            bean.map = mapBeanArrayList;

            dataList.add(bean);

        }

        setTableData(dataList, heads);
    }

    private void setSpiderWebViewScoresData(StudyReportDetailBean.CompetenceBean[] competenceBeans) {
        float[] scoresClass = new float[competenceBeans.length];
        float[] scoresOwn = new float[competenceBeans.length];

        for (int i = 0; i < competenceBeans.length; i++) {
            StudyReportDetailBean.CompetenceBean competenceBean = competenceBeans[i];
            scoresClass[i] = competenceBean.points.classX;
            scoresOwn[i] = competenceBean.points.own;

            TextView nameTextView = new TextView(getActivity());
            nameTextView.setText(competenceBean.name);
            studyReportBinding.circularLayout.addView(nameTextView);
        }

        studyReportBinding.spiderWebScoreView.setScores(10f,scoresOwn,scoresClass);

    }

    private void setTableData(ArrayList<StudyReportDetailBean.DataBean> dataList, String[] heads) {
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
                tableView.addContent(dataBean.KN,mapBean.NO,mapBean.points + "",mapBean.score + "",mapBean.avg + "",dataBean.process * 100 +"%");
            }
        }

        tableView.setRowTypes(rowTypes);
        tableView.refreshTable();
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
//        CommonNotices.show(data.toString());
        setSpiderWebViewScoresData(data.competence.toArray(new StudyReportDetailBean.CompetenceBean[data.competence.size()]));
        setTableData((ArrayList<StudyReportDetailBean.DataBean>) data.data,heads);
    }

    public void setPracticeId(int id) {
        if (homeworkPresenter == null){
            homeworkPresenter = new HomeworkPresenter(this);
        }
        homeworkPresenter.getStudyReportDetail(id);
    }
}
