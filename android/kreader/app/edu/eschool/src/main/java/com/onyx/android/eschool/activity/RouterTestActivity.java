package com.onyx.android.eschool.activity;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.libsetting.data.wifi.AccessPoint;
import com.onyx.android.libsetting.manager.WifiAdmin;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.SyncTimeBySntpRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import de.halfbit.tinymachine.StateHandler;
import de.halfbit.tinymachine.TinyMachine;
import de.halfbit.tinymachine.StateHandler.Type;

import static android.net.NetworkInfo.DetailedState.CONNECTED;

public class RouterTestActivity extends Activity {
    private static final int STATE_INIT = 0;
    private static final int STATE_WIFI_ENABLED = 1;
    private static final int STATE_WIFI_CONNECTED = 2;
    private static final int STATE_NTP_SYNC = 3;
    private static final int STATE_ALARM_SET = 4;
    private static final int STATE_ALARM_TRIGGERED = 5;
    private static final int STATE_ALARM_TESTING = 6;

    private static final String TAG = RouterTestActivity.class.getSimpleName();
    private TinyMachine tinyMachine = new TinyMachine(new RouterStateHandler(), STATE_INIT);
    private WifiAdmin wifiAdmin;
    private static final String ONYX_WIFI_TEST_ROUTER_SSID = "onyx-edu-test";
    private static final String ONYX_WIFI_TEST_ROUTER_PW = "OnyxWpa2009";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_test);
        initUMeng();
        DeviceUtils.turnOffSystemPMSettings(this);
        initReceiver(this);
        triggerInitEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWifiAdmin().unregisterReceiver();
    }

    private void initUMeng() {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setCheckDevice(true);
        MobclickAgent.setLatencyWindow(0);
    }

    private WifiAdmin getWifiAdmin() {
        if (wifiAdmin == null) {
            wifiAdmin = new WifiAdmin(this);
        }
        return wifiAdmin;
    }

    private void initReceiver(final Context context) {
        getWifiAdmin().registerReceiver();
        getWifiAdmin().setCallback(new WifiAdmin.Callback() {
            @Override
            public void onWifiStateChange(boolean isWifiEnable) {
                if (isWifiEnable && tinyMachine.getCurrentState() == STATE_INIT) {
                    tinyMachine.transitionTo(STATE_WIFI_ENABLED);
                }
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                if (tinyMachine.getCurrentState() == STATE_WIFI_ENABLED) {
                    actionOnWiFiScanResultReady(scanResult);
                }
            }

            @Override
            public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {

            }

            @Override
            public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {
                if (state == CONNECTED && tinyMachine.getCurrentState() == STATE_WIFI_ENABLED) {
                    tinyMachine.transitionTo(STATE_WIFI_CONNECTED);
                }
            }
        });
    }

    private void triggerInitEvent() {
        NetworkUtil.enableWiFi(this, false);
        tinyMachine.fireEvent("init");
    }

    public void actionOnInitState() {
        NetworkUtil.enableWiFi(this, true);
    }

    public void actionOnWifiEnabled() {
        getWifiAdmin().triggerWifiScan();
    }

    public void actionOnWiFiScanResultReady(final List<AccessPoint> scanResultList) {
        for (AccessPoint accessPoint : scanResultList) {
            Log.e(TAG, accessPoint.getScanResult().SSID);
            if (accessPoint.getScanResult().SSID.equalsIgnoreCase(ONYX_WIFI_TEST_ROUTER_SSID)) {
                accessPoint.setPassword(ONYX_WIFI_TEST_ROUTER_PW);
                wifiAdmin.connectWifi(accessPoint);
                Log.e(TAG, "Connecting to AP: " + accessPoint.getScanResult().SSID);
                return;
            }
        }
        getWifiAdmin().triggerWifiScan();
    }

    public void actionOnWifiConnected() {
        final SyncTimeBySntpRequest request = new SyncTimeBySntpRequest(3);
        SchoolApp.getSchoolCloudStore().submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                tinyMachine.transitionTo(STATE_NTP_SYNC);
            }
        });
    }

    public void actionOnSntpSync() {

    }

    // Define state handler class with handler methods
    public class RouterStateHandler {

        @StateHandler(state = STATE_INIT)
        public void onEventStateInit(String event, TinyMachine tm) {
            actionOnInitState();
        }

        @StateHandler(state = STATE_WIFI_ENABLED, type = Type.OnEntry)
        public void onEntryStateWifiEnabled() {
            actionOnWifiEnabled();
        }

        @StateHandler(state = STATE_WIFI_CONNECTED, type = Type.OnEntry)
        public void onEntryStateWifiConnected() {
            actionOnWifiConnected();
        }

        @StateHandler(state = STATE_NTP_SYNC, type = Type.OnEntry)
        public void onEntryStateSntpSync() {
            actionOnSntpSync();
        }

    }

}
