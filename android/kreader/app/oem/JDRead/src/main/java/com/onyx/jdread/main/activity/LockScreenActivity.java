package com.onyx.jdread.main.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityLockScreenBinding;
import com.onyx.jdread.main.common.ManagerActivityUtils;
import com.onyx.jdread.main.event.PasswordIsCorrectEvent;
import com.onyx.jdread.main.model.LockScreenModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-2.
 */

public class LockScreenActivity extends AppCompatActivity {

    private ActivityLockScreenBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        LockScreenModel lockScreenModel = new LockScreenModel(getEventBus());
        dataBinding.setScreenLockModel(lockScreenModel);
    }

    private void initView() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);
    }

    @Subscribe
    public void onPasswordIsCorrectEvent(PasswordIsCorrectEvent event) {
        ManagerActivityUtils.startMainActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
