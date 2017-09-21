package com.onyx.android.monitor;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.monitor.databinding.ActivityPreviewBinding;
import com.onyx.android.monitor.event.MenuKeyEvent;
import com.onyx.android.sdk.common.request.WakeLockHolder;

import org.greenrobot.eventbus.EventBus;

public class PreviewActivity extends Activity {
    static final String TAG = PreviewActivity.class.getSimpleName();
    ActivityPreviewBinding binding;
    private WakeLockHolder wakeLockHolder = new WakeLockHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireWakelock();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(binding.container.getId(), PreviewFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakelock();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode) {
            EventBus.getDefault().post(new MenuKeyEvent());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void acquireWakelock() {
        wakeLockHolder.acquireWakeLock(this, WakeLockHolder.WAKEUP_FLAGS | WakeLockHolder.ON_AFTER_RELEASE, TAG);
    }

    private void releaseWakelock() {
        wakeLockHolder.releaseWakeLock();
    }
}
