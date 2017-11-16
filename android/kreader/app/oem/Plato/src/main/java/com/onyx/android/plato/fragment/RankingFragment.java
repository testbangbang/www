package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.databinding.RankingBinding;
import com.onyx.android.plato.event.ToMainFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-10-17.
 */

public class RankingFragment extends BaseFragment implements View.OnClickListener {

    private RankingBinding rankingBinding;

    @Override
    protected void loadData() {
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        rankingBinding = (RankingBinding) binding;
        rankingBinding.rankingTitleBar.titleBarRecord.setVisibility(View.GONE);
        rankingBinding.rankingTitleBar.titleBarSubmit.setVisibility(View.GONE);
        rankingBinding.rankingTitleBar.setTitle(getString(R.string.total_score_ranking));
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
