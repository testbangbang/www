package com.onyx.android.eschool.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.AuthTokenAction;
import com.onyx.android.libsetting.data.wifi.AccessPoint;
import com.onyx.android.libsetting.manager.WifiAdmin;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.SyncTimeBySntpRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.List;

import de.halfbit.tinymachine.StateHandler;
import de.halfbit.tinymachine.TinyMachine;
import de.halfbit.tinymachine.StateHandler.Type;

import static android.net.NetworkInfo.DetailedState.CONNECTED;

public class RouterTestActivity extends Activity {
    private static final int STATE_INIT                         = 0;
    private static final int STATE_WIFI_INIT_ENABLED            = 1;
    private static final int STATE_WIFI_WAIT_FOR_SCAN_RESULT    = 2;
    private static final int STATE_WIFI_SCAN_RESULT_READY       = 3;
    private static final int STATE_WIFI_WAIT_FOR_CONNECTED      = 4;
    private static final int STATE_WIFI_CONNECTED               = 5;
    private static final int STATE_NTP_SYNC                     = 6;
    private static final int STATE_ALARM_SET                    = 7;
    private static final int STATE_ALARM_TRIGGERED              = 8;
    private static final int STATE_WAIT_FOR_ENABLE_AFTER_ALARM  = 9;
    private static final int STATE_WIFI_ENABLED_AFTER_ALARM     = 10;
    private static final int STATE_WAIT_FOR_CONNECTED_AFTER_ALARM = 11;
    private static final int STATE_CONNECTED_AFTER_ALARM        = 12;
    private static final int STATE_START_TESTING                = 8;
    private static final int STATE_CLOUD_AUTH                   = 9;
    private static final int STATE_CLOUD_DOWNLOAD               = 10;

    private static final String TAG = RouterTestActivity.class.getSimpleName();
    private TinyMachine tinyMachine = new TinyMachine(new RouterStateHandler(), STATE_INIT);
    private WifiAdmin wifiAdmin;
    private static final String ONYX_WIFI_TEST_ROUTER_SSID = "onyx-zeng";
    private static final String ONYX_WIFI_TEST_ROUTER_PW = "OnyxWpa2009";

    private static int ALARM_START_INTERVAL_TIME = 1;
    private static long ALARM_REPEAT_INTERVAL_TIME = 60 * 1000;
    private static String ALARM_INTENT_ACTION = "com.action.router.AlarmManager";
    private BroadcastReceiver alarmReceiver;

    private AccessPoint lastAccessPoint;

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
                    tinyMachine.transitionTo(STATE_WIFI_INIT_ENABLED);
                }
                if (isWifiEnable && tinyMachine.getCurrentState() == STATE_WAIT_FOR_ENABLE_AFTER_ALARM) {
                    tinyMachine.transitionTo(STATE_WIFI_ENABLED_AFTER_ALARM);
                }
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                if (tinyMachine.getCurrentState() == STATE_WIFI_WAIT_FOR_SCAN_RESULT) {
                    tinyMachine.transitionTo(STATE_WIFI_SCAN_RESULT_READY);
                    actionOnWiFiScanResultReady(scanResult);
                }
                if (tinyMachine.getCurrentState() == STATE_ALARM_TRIGGERED) {

                }
            }

            @Override
            public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {

            }

            @Override
            public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {
                if (state == CONNECTED && tinyMachine.getCurrentState() == STATE_WIFI_WAIT_FOR_CONNECTED) {
                    tinyMachine.transitionTo(STATE_WIFI_CONNECTED);
                }
                if (state == CONNECTED && tinyMachine.getCurrentState() == STATE_WAIT_FOR_CONNECTED_AFTER_ALARM) {
                    tinyMachine.transitionTo(STATE_CONNECTED_AFTER_ALARM);
                }
            }
        });

        alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ALARM_INTENT_ACTION)) {
                    onReceivedAlarm();
                }
            }
        };
        final IntentFilter alarmFilter = new IntentFilter();
        alarmFilter.addAction(ALARM_INTENT_ACTION);
        registerReceiver(alarmReceiver, alarmFilter);
    }

    private void triggerInitEvent() {
        NetworkUtil.enableWiFi(this, false);
        tinyMachine.fireEvent("init");
    }

    public void actionOnInitState() {
        NetworkUtil.enableWiFi(this, true);
    }

    public void actionOnWifiInitEnabled() {
        tinyMachine.transitionTo(STATE_WIFI_WAIT_FOR_SCAN_RESULT);
        getWifiAdmin().triggerWifiScan();
    }

    public void actionOnWiFiScanResultReady(final List<AccessPoint> scanResultList) {
        for (AccessPoint accessPoint : scanResultList) {
            Log.e(TAG, accessPoint.getScanResult().SSID);
            if (accessPoint.getScanResult().SSID.equalsIgnoreCase(ONYX_WIFI_TEST_ROUTER_SSID)) {
                lastAccessPoint = accessPoint;
                accessPoint.setPassword(ONYX_WIFI_TEST_ROUTER_PW);
                getWifiAdmin().connectWifi(accessPoint);
                tinyMachine.transitionTo(STATE_WIFI_WAIT_FOR_CONNECTED);
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
        triggerRepeatedAlarm();
        NetworkUtil.enableWiFi(this, false);
        tinyMachine.transitionTo(STATE_ALARM_SET);
    }

    public void actionOnAlarmTriggered() {
        tinyMachine.transitionTo(STATE_WAIT_FOR_ENABLE_AFTER_ALARM);
        NetworkUtil.enableWiFi(this, true);
    }

    public void actionOnWaitForWiFiEnableAfterAlarm() {

    }

    public void actionOnWiFiEnabledAfterAlarm() {
        getWifiAdmin().connectWifi(lastAccessPoint);
        tinyMachine.transitionTo(STATE_WAIT_FOR_CONNECTED_AFTER_ALARM);
    }

    public void actionOnConnectedAfterAlarm() {
        final AuthTokenAction authTokenAction = new AuthTokenAction();
        authTokenAction.execute(SchoolApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    // Define state handler class with handler methods
    public class RouterStateHandler {
        @StateHandler(state = STATE_INIT)
        public void onEventStateInit(String event, TinyMachine tm) {
            Log.e(TAG, "state changed to STATE_INIT");
            actionOnInitState();
        }

        @StateHandler(state = STATE_WIFI_INIT_ENABLED, type = Type.OnEntry)
        public void onEntryStateWifiInitEnabled() {
            Log.e(TAG, "state changed to STATE_WIFI_INIT_ENABLED");
            actionOnWifiInitEnabled();
        }

        @StateHandler(state = STATE_WIFI_SCAN_RESULT_READY, type = Type.OnEntry)
        public void onEntryStateScanResultReady() {
            Log.e(TAG, "state changed to STATE_WIFI_SCAN_RESULT_READY");
        }

        @StateHandler(state = STATE_WIFI_CONNECTED, type = Type.OnEntry)
        public void onEntryStateWifiConnected() {
            Log.e(TAG, "state changed to STATE_WIFI_CONNECTED");
            actionOnWifiConnected();
        }

        @StateHandler(state = STATE_NTP_SYNC, type = Type.OnEntry)
        public void onEntryStateSntpSync() {
            Log.e(TAG, "state changed to STATE_NTP_SYNC");
            actionOnSntpSync();
        }

        @StateHandler(state = STATE_ALARM_SET, type = Type.OnEntry)
        public void onEntryStateAlarmSet() {
            Log.e(TAG, "state changed to STATE_ALARM_SET");
            actionOnSntpSync();
        }

        @StateHandler(state = STATE_ALARM_TRIGGERED, type = Type.OnEntry)
        public void onEntryStateAlarmTriggered() {
            Log.e(TAG, "state changed to STATE_ALARM_TRIGGERED");
            actionOnAlarmTriggered();
        }

        @StateHandler(state = STATE_WIFI_ENABLED_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateWiFiEnabledAfterAlarm() {
            Log.e(TAG, "state changed to STATE_WIFI_ENABLED_AFTER_ALARM");
            actionOnWiFiEnabledAfterAlarm();
        }

        @StateHandler(state = STATE_CONNECTED_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateConnectedAfterAlarm() {
            Log.e(TAG, "state changed to STATE_CONNECTED_AFTER_ALARM");
            actionOnConnectedAfterAlarm();
        }

        @StateHandler(state = STATE_WAIT_FOR_ENABLE_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateWaitForEnableAfterAlarm() {
            Log.e(TAG, "state changed to STATE_WAIT_FOR_ENABLE_AFTER_ALARM");
        }


    }

    private void triggerRepeatedAlarm() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        minute += ALARM_START_INTERVAL_TIME;
        if (minute >= 60) {
            int hour = c.get(Calendar.HOUR_OF_DAY);
            c.set(Calendar.HOUR_OF_DAY, hour + 1);
            minute %= 60;
        }
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setTimeZone("Asia/Hong_Kong");
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), ALARM_REPEAT_INTERVAL_TIME, getAlarmPendingIntent());
    }

    private PendingIntent getAlarmPendingIntent() {
        Intent intent = new Intent(ALARM_INTENT_ACTION);
        PendingIntent startAlarmPendingIntent = PendingIntent.getBroadcast(RouterTestActivity.this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return startAlarmPendingIntent;
    }

    private void onReceivedAlarm() {
        tinyMachine.transitionTo(STATE_ALARM_TRIGGERED);
    }

}
