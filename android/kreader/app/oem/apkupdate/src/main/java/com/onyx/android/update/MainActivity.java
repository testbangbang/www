package com.onyx.android.update;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WAITING_TIME = 3000;
    private static final int REBOOT_WAITING_TIME = 1000;
    private static final int REBOOT = 0x101;
    private static final int REINSTALL = 0x102;
    private static final int REBOOT_TIME = 6;
    public static final String APK_PATH = "apk_path";
    private Handler handler;
    private TextView updateMessage;
    private TextView updatingAppMessage;
    private int rebootCount = REBOOT_TIME;
    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        updatingAppMessage = (TextView) findViewById(R.id.updating_app_message);
        updatingAppMessage.setOnClickListener(this);
        updateMessage = (TextView) findViewById(R.id.update_message);
    }

    private void initData() {
        apkPath = getIntent().getStringExtra(APK_PATH);
        if (StringUtils.isNullOrEmpty(apkPath)) {
            finish();
            return;
        }
        Log.i(TAG, apkPath);
        handler = new MyHandler();
        startInstallThread();
    }

    private void setUpdatingAppMessage(String message, Object tag) {
        updatingAppMessage.setText(message);
        updatingAppMessage.setTag(tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startInstallThread() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                installApp();
            }
        }, WAITING_TIME);
    }

    private void installApp() {
        String result = SilentInstall.installSilent(this, apkPath);
        File file = new File(apkPath);
        if (StringUtils.isNullOrEmpty(result) && file.delete()) {
            rebootDevice();
        } else {
            setUpdatingAppMessage(result, REINSTALL);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REBOOT:
                    rebootDevice();
                    break;
            }
        }
    }

    private void rebootDevice() {
        if (rebootCount <= 0) {
            SilentInstall.rebootDevice(MainActivity.this);
            return;
        }
        if (rebootCount == REBOOT_TIME) {
            setUpdatingAppMessage(getString(R.string.update_success), null);
        } else {
            String message = String.format(getString(R.string.reboot_message), rebootCount);
            setUpdatingAppMessage(message, null);
        }

        rebootCount--;
        handler.sendEmptyMessageDelayed(REBOOT, REBOOT_WAITING_TIME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updating_app_message:
                onUpdateAppMessageClick(v);
                break;
        }
    }

    public void onUpdateAppMessageClick(View v) {
        Object object = updatingAppMessage.getTag();
        if (object != null) {
            int tag = (Integer) object;
            if (tag == REINSTALL) {
                startInstallThread();
                setUpdatingAppMessage(getString(R.string.updating_app), null);
            }
        }
    }
}
