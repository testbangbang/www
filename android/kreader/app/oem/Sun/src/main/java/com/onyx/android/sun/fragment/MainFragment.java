package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sun.R;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.databinding.MainBinding;
import com.onyx.android.sun.interfaces.MainFragmentView;
import com.onyx.android.sun.presenter.MainFragmentPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-9-29.
 */

public class MainFragment extends BaseFragment implements MainFragmentView, View.OnClickListener {

    private MainBinding mainBinding;
    private MainFragmentPresenter presenter;

    @Override
    protected void loadData() {
        presenter = new MainFragmentPresenter(this);
        presenter.getPractices();
        presenter.getSubjectScore();
    }

    @Override
    protected void initView(ViewDataBinding rootView) {
        mainBinding = (MainBinding) rootView;
    }

    @Override
    protected void initListener() {
        mainBinding.setListener(this);
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_main;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_all_task:
                // TODO: 17-10-9 view all
                break;
            case R.id.grade_ranking_title:
            case R.id.grade_ranking:
                // TODO: 17-10-9 ranking
                break;
        }
    }

    @Override
    public void setPractices(List<ContentBean> list) {

    }

    @Override
    public void setSubjectScore(Map<String, Float> subjectScoreMap) {
        if (!CollectionUtils.isNullOrEmpty(subjectScoreMap)){
            mainBinding.radarChart.setAxis(subjectScoreMap);
            mainBinding.radarChart.setAxisTick(mainBinding.radarChart.getAxisMax() - 0.5f);
        }
    }
}
