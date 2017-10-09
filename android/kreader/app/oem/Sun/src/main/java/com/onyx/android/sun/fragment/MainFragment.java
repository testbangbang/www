package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.databinding.MainBinding;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hehai on 17-9-29.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener {

    private Map<String, Float> scores = new TreeMap<>();
    private MainBinding mainBinding;

    @Override
    protected void loadData() {
        scores.put("语文", 80F);
        scores.put("数学", 90F);
        scores.put("英语", 80F);
        scores.put("物理", 100F);
        scores.put("化学", 80F);
        scores.put("生物", 60F);
        scores.put("政治", 50F);
        scores.put("历史", 40F);
        scores.put("地理", 70F);
    }

    @Override
    protected void initView(ViewDataBinding rootView) {
        mainBinding = (MainBinding) rootView;
        mainBinding.radarChart.setAxis(scores);
        mainBinding.radarChart.setAxisTick(mainBinding.radarChart.getAxisMax()-0.5f);
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
        switch (v.getId()){
            case R.id.view_all_task:
                // TODO: 17-10-9 view all
                break;
            case R.id.grade_ranking_title:
            case R.id.grade_ranking:
                // TODO: 17-10-9 ranking
                break;
        }
    }
}
