package com.onyx.android.note.actions.common;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ming on 2017/6/8.
 */

public class WifiConnectAction <T extends Activity> extends BaseNoteAction<T> {

    private long waitForConnectedTime;
    private long checkWifiPeriodTime;
    private String loadingTitle;
    private boolean showDialog = false;

    private OnyxCustomDialog loadingDialog;
    private Timer timer;
    private int index;

    public WifiConnectAction(long waitForConnectedTime, long checkWifiPeriodTime, String loadingTitle, boolean showDialog) {
        this.waitForConnectedTime = waitForConnectedTime;
        this.checkWifiPeriodTime = checkWifiPeriodTime;
        this.loadingTitle = loadingTitle;
        this.showDialog = showDialog;
    }

    @Override
    public void execute(final T activity, final BaseCallback baseCallback) {
        final int checkCount = (int) (waitForConnectedTime / checkWifiPeriodTime);
        Device.currentDevice().enableWifiDetect(activity, true);
        NetworkUtil.enableWiFi(activity, true);
        showLoadingDialog(activity);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                index++;
                if (NetworkUtil.isWiFiConnected(activity)) {
                    quit(activity, baseCallback, true);
                }else if (index > checkCount){
                    quit(activity, baseCallback, false);
                }
            }
        }, checkWifiPeriodTime, checkWifiPeriodTime);
    }

    private void showLoadingDialog(Context context) {
        if(!showDialog) {
            return;
        }
        loadingDialog = OnyxCustomDialog.getLoadingDialog(context, loadingTitle);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog == null) {
            return;
        }
        loadingDialog.hide();
        loadingDialog = null;
    }

    private void quit(final Context context, final BaseCallback baseCallback, final boolean isConnected) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                timer.cancel();
                timer = null;
                BaseCallback.invoke(baseCallback, null, isConnected ? null : new Throwable("wifi connect fail"));
            }
        });
    }

}
