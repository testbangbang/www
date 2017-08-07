package com.onyx.edu.manager.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.onyx.edu.manager.R;

public class DoubleClickExitHelper {
    private Activity activity;
    private boolean isOnKeyBacking;
    private Handler handler;
    private Toast backToast;

    public DoubleClickExitHelper(Activity activity) {
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if (isOnKeyBacking) {
            handler.removeCallbacks(onBackTimeRunnable);
            getBackToast().cancel();
            activity.finish();
            return true;
        } else {
            isOnKeyBacking = true;
            getBackToast().show();
            handler.postDelayed(onBackTimeRunnable, 2500);
            return true;
        }
    }

    private Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isOnKeyBacking = false;
            getBackToast().cancel();
        }
    };

    @SuppressLint("ShowToast")
    private Toast getBackToast() {
        if (backToast == null) {
            backToast = Toast.makeText(activity.getApplicationContext(), R.string.double_back_press_tip, Toast.LENGTH_SHORT);
        }
        return backToast;
    }
}

