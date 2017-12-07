package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.RankingAdapter;
import com.onyx.android.plato.databinding.RankingBinding;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-10-17.
 */

public class RankingFragment extends BaseFragment implements View.OnClickListener {

    private RankingBinding rankingBinding;
    private RankingAdapter adapter;

    @Override
    protected void loadData() {
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        rankingBinding = (RankingBinding) binding;
        rankingBinding.rankingTitleBar.titleBarRecord.setVisibility(View.GONE);
        rankingBinding.rankingTitleBar.titleBarSubmit.setVisibility(View.GONE);
        rankingBinding.rankingTitleBar.setTitle(getString(R.string.total_score_ranking));
        rankingBinding.rankingRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        decoration.setDrawLine(true);
        rankingBinding.rankingRecycler.addItemDecoration(decoration);
        adapter = new RankingAdapter();
        rankingBinding.rankingRecycler.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        rankingBinding.rankingTitleBar.titleBarTitle.setOnClickListener(this);
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_ranking;
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
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_title:
                EventBus.getDefault().post(new ToMainFragmentEvent());
                break;
        }
    }
}
