package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.databinding.EmptyBinding;
import com.onyx.android.plato.event.RefreshFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/11/20.
 */

public class EmptyFragment extends BaseFragment implements View.OnClickListener {
    private EmptyBinding emptyBinding;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        emptyBinding = (EmptyBinding)binding;
    }

    @Override
    protected void initListener() {
        emptyBinding.loadAgain.setOnClickListener(this);
    }

    @Override
    protected int getRootView() {
        return R.layout.empty_fragment_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new RefreshFragmentEvent());
    }
}
