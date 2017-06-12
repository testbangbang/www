package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ming on 2017/6/8.
 */

public class WifiConnectAction extends BaseAction {

    private long waitForConnectedTime;
    private long checkWifiPeriodTime;
    private String loadingTitle;

    private OnyxCustomDialog loadingDialog;
    private Timer timer;
    private int index;

    public WifiConnectAction(long waitForConnectedTime, long checkWifiPeriodTime, String loadingTitle) {
        this.waitForConnectedTime = waitForConnectedTime;
        this.checkWifiPeriodTime = checkWifiPeriodTime;
        this.loadingTitle = loadingTitle;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final int checkCount = (int) (waitForConnectedTime / checkWifiPeriodTime);
        Device.currentDevice().enableWifiDetect(readerDataHolder.getContext(), true);
        NetworkUtil.enableWiFi(readerDataHolder.getContext(), true);
        loadingDialog = OnyxCustomDialog.getLoadingDialog(readerDataHolder.getContext(), loadingTitle);
        loadingDialog.show();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                index++;
                if (NetworkUtil.isWiFiConnected(readerDataHolder.getContext())) {
                    quit(readerDataHolder, baseCallback, true);
                }else if (index > checkCount){
                    quit(readerDataHolder, baseCallback, false);
                }
            }
        }, checkWifiPeriodTime, checkWifiPeriodTime);
    }

    private void quit(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback, final boolean isConnected) {
        readerDataHolder.getReader().getLooperHandler().post(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hide();
                loadingDialog = null;
                timer.cancel();
                timer = null;
                BaseCallback.invoke(baseCallback, null, isConnected ? null : new Throwable("wifi connect fail"));
            }
        });

    }

}
