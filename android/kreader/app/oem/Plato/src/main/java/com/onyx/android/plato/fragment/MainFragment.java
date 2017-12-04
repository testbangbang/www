package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.TodayTaskAdapter;
import com.onyx.android.plato.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.databinding.MainBinding;
import com.onyx.android.plato.event.ToHomeworkEvent;
import com.onyx.android.plato.event.ToRankingEvent;
import com.onyx.android.plato.interfaces.MainFragmentView;
import com.onyx.android.plato.presenter.MainFragmentPresenter;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hehai on 17-9-29.
 */

public class MainFragment extends BaseFragment implements MainFragmentView, View.OnClickListener {

    private MainBinding mainBinding;
    private MainFragmentPresenter presenter;
    private TodayTaskAdapter adapter;

    @Override
    protected void loadData() {
        presenter = new MainFragmentPresenter(this);
        presenter.getPractices();
        presenter.getSubjectScore();
    }

    @Override
    protected void initView(ViewDataBinding rootView) {
        mainBinding = (MainBinding) rootView;
        Date date = new Date(System.currentTimeMillis());
        mainBinding.setDate(DateTimeUtil.formatDate(date, new SimpleDateFormat("MM-dd", Locale.getDefault())));
        mainBinding.todayTaskRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        mainBinding.todayTaskRecycler.addItemDecoration(dividerItemDecoration);
        adapter = new TodayTaskAdapter();
        mainBinding.todayTaskRecycler.setAdapter(adapter);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_all_task:
                EventBus.getDefault().post(new ToHomeworkEvent());
                break;
            case R.id.grade_ranking_title:
            case R.id.grade_ranking:
                EventBus.getDefault().post(new ToRankingEvent());
                break;
        }
    }

    @Override
    public void setPractices(List<ContentBean> list) {
        adapter.setData(list);
    }

    @Override
    public void setSubjectScore(PersonalAbilityResultBean.DataBean data, Map<String, Float> subjectScoreMap) {
        if (!CollectionUtils.isNullOrEmpty(subjectScoreMap)) {
            mainBinding.radarChart.setAxis(subjectScoreMap);
            mainBinding.radarChart.setAxisTick(mainBinding.radarChart.getAxisMax() * 0.99f);
        }

        mainBinding.totalScore.setText(String.valueOf(data.score));
        mainBinding.fullScore.setText(String.format(getString(R.string.full_score), data.totalPoints));
        mainBinding.gradeRanking.setText(data.gradeRank + "/" + data.gredeSize);
    }
}
