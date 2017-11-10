package com.onyx.android.update;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.download.onyxdownloadservice.DownloadCallback;
import com.onyx.download.onyxdownloadservice.DownloadErrorEvent;
import com.onyx.download.onyxdownloadservice.DownloadTaskManager;
import com.onyx.download.onyxdownloadservice.ReportDownloadProcessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener, DownloadCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WAITING_TIME = 3000;
    private static final int REBOOT_WAITING_TIME = 1000;
    private static final int REBOOT = 0x101;
    private static final int REINSTALL = 0x102;
    private static final int REBOOT_TIME = 6;
    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String UPDATE_MESSAGE = "message";
    public static final int DOWNLOAD_COMPLETE = 100;
    private Handler handler;
    private String downloadUrl;
    private String message;
    private TextView updateMessage;
    private TextView updatingAppMessage;
    private int rebootCount = REBOOT_TIME;
    private int reference = -1;
    private boolean installState = false;

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

        updateMessage = (TextView)findViewById(R.id.update_message);
        updateMessage.setText(message);
    }

    private void setUpdatingAppMessage(String message,Object tag) {
        updatingAppMessage.setText(message);
        updatingAppMessage.setTag(tag);
    }

    private void initData() {
        EventBus.getDefault().register(this);
        downloadUrl = getIntent().getStringExtra(DOWNLOAD_URL);
        if (StringUtils.isNullOrEmpty(downloadUrl)) {
            finish();
            return;
        }
        message = getIntent().getStringExtra(UPDATE_MESSAGE);
        Log.i(TAG, downloadUrl);
        handler = new MyHandler();
        reference = ApkUpdate.downloadAPK(downloadUrl, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void startInstallThread() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                installApp();
            }
        }, WAITING_TIME);
    }

    private void installApp() {
        String result = SilentInstall.installSilent(this, ApkUpdate.APK_DOWNLOAD_PATH);
        if (StringUtils.isNullOrEmpty(result)) {
            rebootDevice();
        } else {
            setUpdatingAppMessage(result,REINSTALL);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
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
            setUpdatingAppMessage(getString(R.string.update_success),null);
        } else {
            String message = String.format(getString(R.string.reboot_message), rebootCount);
            setUpdatingAppMessage(message,null);
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
                setUpdatingAppMessage(getString(R.string.updating_app),null);
            }
        }
    }

    @Override
    public void progressChanged(int reference, String title, String remoteUri, String localUri, int state, long finished, long total, long percentage) {
    }

    @Subscribe
    public void onReportDownloadProcessEvent(ReportDownloadProcessEvent event) {
        if (event != null && this.reference == event.getReference() && !installState) {
            String progress = String.format(getString(R.string.download_progress), event.getPercentage());
            setUpdatingAppMessage(progress + "%",null);
            if (event.getPercentage() == DOWNLOAD_COMPLETE) {
                setUpdatingAppMessage(getString(R.string.updating_app),null);
                installState = true;
                startInstallThread();
            }
        }
    }

    @Subscribe
    public void onDownloadErrorEvent(DownloadErrorEvent errorEvent) {
        if (errorEvent != null) {
            Log.i(TAG, errorEvent.getMessage());
        }
    }
}
