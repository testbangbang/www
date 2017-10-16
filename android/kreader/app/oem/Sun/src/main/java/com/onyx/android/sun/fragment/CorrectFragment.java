package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.databinding.CorrectDataBinding;
import com.onyx.android.sun.event.TimerEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2017/10/16.
 */

public class CorrectFragment extends BaseFragment {
    private CorrectDataBinding correctDataBinding;
    private boolean hasCorrected;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        correctDataBinding = (CorrectDataBinding) binding;
        correctDataBinding.setCorrected(hasCorrected);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.correct_fragment_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnTimerEvent(TimerEvent event) {
        Log.d("---------", "OnTimerEvent: ");
        correctDataBinding.setCount(event.getResult());
    }

    public void setStartTimer(boolean hasCorrected) {
        this.hasCorrected = hasCorrected;
        TimerEvent timerEvent = new TimerEvent();
        timerEvent.timeCountDown(3);
    }
}
