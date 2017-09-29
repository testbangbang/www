package com.onyx.android.sample.fragment;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.onyx.android.sample.events.StopTestEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by wangxu on 17-9-28.
 */

public class BaseTestFragment extends Fragment {

    public Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void stopTest() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopEvent(StopTestEvent event) {
        stopTest();
    }
}
