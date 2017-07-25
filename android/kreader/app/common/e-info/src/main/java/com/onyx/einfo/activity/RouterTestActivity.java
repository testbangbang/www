package com.onyx.einfo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.AuthTokenAction;
import com.onyx.einfo.action.DownloadAction;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.request.cloud.SyncTimeBySntpRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private static final int STATE_START_TESTING                = 13;
    private static final int STATE_CLOUD_AUTH                   = 14;
    private static final int STATE_CLOUD_DOWNLOAD               = 15;

    private static final String TAG = RouterTestActivity.class.getSimpleName();
    private TinyMachine tinyMachine = new TinyMachine(new RouterStateHandler(), STATE_INIT);
    private WifiAdmin wifiAdmin;
    private static final String ONYX_WIFI_TEST_ROUTER_SSID = "onyx-zeng";
    private static final String ONYX_WIFI_TEST_ROUTER_PW = "OnyxWpa2009";

    private static int ALARM_START_INTERVAL_TIME = 1;
    private static long ALARM_REPEAT_INTERVAL_TIME = 60 * 1000;
    private static long POST_INTERVAL = 30 * 1000;
    private static String ALARM_INTENT_ACTION = "com.action.router.AlarmManager";
    private BroadcastReceiver alarmReceiver;

    private LibraryDataHolder libraryDataHolder;

    private AccessPoint lastAccessPoint;
    private static final String URL =
            "http://oa.o-in.me:9002/repo/3ec511d5-5fdd-49ab-bd9b-32d2e09ff12a/efe936ce98141a82a8b214ef6690fe34560aa3f6/?file_name=%E7%A2%A7%E5%B2%A9%E5%BD%95.pdf&op=download&t=24eae196b9&p=/%E6%B5%8B%E8%AF%95%E6%96%87%E6%A1%A3/PDF%E6%B5%8B%E8%AF%95%E6%96%87%E6%A1%A3/%E7%A2%A7%E5%B2%A9%E5%BD%95.pdf";

    private long testingCount = 0;
    private long wifiConnectedDuration = 0;
    private float averageWifiConnectedDuration = 0;
    private long authDuration = 0;
    private long downloadDuration = 0;
    private float averageDownloadDuration = 0;
    private long downloadSucceedCount = 0;
    private long downloadFailedCount = 0;
    private long apFailedCount = 0;

    private TextView stateView;
    private TextView connectDurationView;
    private TextView downloadDurationView;
    private TextView downloadFailedView;
    private TextView apFailedView;

    private WakeLockHolder wakeLockHolder = new WakeLockHolder();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_test);
        initView();
        initUMeng();
        DeviceUtils.turnOffSystemPMSettings(this);
        initReceiver(this);
        triggerInitEvent();
        initWakelock();
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
        cleanup();
    }

    private void initView() {
        stateView = (TextView)findViewById(R.id.textView_state);
        connectDurationView = (TextView)findViewById(R.id.textView_connect_duration);
        downloadDurationView = (TextView)findViewById(R.id.textView_download_duration);
        downloadFailedView = (TextView)findViewById(R.id.textView_download_failed_count);
        apFailedView = (TextView)findViewById(R.id.textView_ap_failed_count);
    }

    private void initWakelock() {
        wakeLockHolder.acquireWakeLock(this, TAG);
    }

    private void cleanup() {
        getWifiAdmin().unregisterReceiver();
        if (alarmReceiver != null) {
            unregisterReceiver(alarmReceiver);
        }
    }

    private void initUMeng() {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setCheckDevice(true);
        MobclickAgent.setLatencyWindow(0);
    }

    private LibraryDataHolder getLibraryDataHolder() {
        if (libraryDataHolder == null) {
            libraryDataHolder = new LibraryDataHolder(this);
        }
        return libraryDataHolder;
    }

    private long reportCurrentTimeStamp() {
        return System.currentTimeMillis();
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
            public void onWifiStateChange(boolean isWifiEnable,int wifiExtraState) {
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
                if (state == NetworkInfo.DetailedState.FAILED) {
                    reportWifiFailed();
                }
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
        InfoApp.getCloudStore().submitRequest(this, request, new BaseCallback() {
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
        wifiConnectedDuration = reportCurrentTimeStamp();
    }

    public void actionOnWiFiEnabledAfterAlarm() {
        getWifiAdmin().connectWifi(lastAccessPoint);
        tinyMachine.transitionTo(STATE_WAIT_FOR_CONNECTED_AFTER_ALARM);
    }

    public void actionOnConnectedAfterAlarm() {
        reportApSucceed();
        wifiConnectedDuration = reportCurrentTimeStamp() - wifiConnectedDuration;
        reportAverageConnectTimeToCloud();
        tinyMachine.transitionTo(STATE_START_TESTING);
        authDuration = reportCurrentTimeStamp();
        final AuthTokenAction authTokenAction = new AuthTokenAction();
        authTokenAction.execute(InfoApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                authDuration = reportCurrentTimeStamp() - authDuration;
                tinyMachine.transitionTo(STATE_CLOUD_DOWNLOAD);
                reportAuthSucceed();
            }
        });
    }

    public void actionOnCloudDownload() {
        downloadDuration = reportCurrentTimeStamp();
        final String id = UUID.randomUUID().toString();
        final String filePath = "/mnt/sdcard/Download-" + id;
        final View view = findViewById(android.R.id.content);
        final DownloadAction downloadAction = new DownloadAction(URL, filePath, id);
        downloadAction.execute(getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EpdController.postInvalidate(view, UpdateMode.GC);
                if (e != null) {
                    e.printStackTrace();
                }
                downloadDuration = reportCurrentTimeStamp() - downloadDuration;
                reportDownloadResult(e == null);
                FileUtils.deleteFile(filePath);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tinyMachine.transitionTo(STATE_ALARM_SET);
                    }
                }, POST_INTERVAL);
            }
        });
    }

    // Define state handler class with handler methods
    public class RouterStateHandler {
        @StateHandler(state = STATE_INIT)
        public void onEventStateInit(String event, TinyMachine tm) {
            reportState("state changed to STATE_INIT: " + STATE_INIT);
            actionOnInitState();
        }

        @StateHandler(state = STATE_WIFI_INIT_ENABLED, type = Type.OnEntry)
        public void onEntryStateWifiInitEnabled() {
            reportState("state changed to STATE_WIFI_INIT_ENABLED: " + STATE_WIFI_INIT_ENABLED);
            actionOnWifiInitEnabled();
        }

        @StateHandler(state = STATE_WIFI_SCAN_RESULT_READY, type = Type.OnEntry)
        public void onEntryStateScanResultReady() {
            reportState("state changed to STATE_WIFI_SCAN_RESULT_READY: " + STATE_WIFI_SCAN_RESULT_READY);
        }

        @StateHandler(state = STATE_WIFI_CONNECTED, type = Type.OnEntry)
        public void onEntryStateWifiConnected() {
            reportState("state changed to STATE_WIFI_CONNECTED: " + STATE_WIFI_CONNECTED);
            actionOnWifiConnected();
        }

        @StateHandler(state = STATE_NTP_SYNC, type = Type.OnEntry)
        public void onEntryStateSntpSync() {
            reportState("state changed to STATE_NTP_SYNC: " + STATE_NTP_SYNC);
            actionOnSntpSync();
        }

        @StateHandler(state = STATE_ALARM_SET, type = Type.OnEntry)
        public void onEntryStateAlarmSet() {
            reportState("state changed to STATE_ALARM_SET: " + STATE_ALARM_SET);
            actionOnSntpSync();
        }

        @StateHandler(state = STATE_ALARM_TRIGGERED, type = Type.OnEntry)
        public void onEntryStateAlarmTriggered() {
            reportState("state changed to STATE_ALARM_TRIGGERED: " + STATE_ALARM_TRIGGERED);
            actionOnAlarmTriggered();
        }

        @StateHandler(state = STATE_WIFI_ENABLED_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateWiFiEnabledAfterAlarm() {
            reportState("state changed to STATE_WIFI_ENABLED_AFTER_ALARM: " + STATE_WIFI_ENABLED_AFTER_ALARM);
            actionOnWiFiEnabledAfterAlarm();
        }

        @StateHandler(state = STATE_CONNECTED_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateConnectedAfterAlarm() {
            reportState("state changed to STATE_CONNECTED_AFTER_ALARM: " + STATE_CONNECTED_AFTER_ALARM);
            actionOnConnectedAfterAlarm();
        }

        @StateHandler(state = STATE_WAIT_FOR_ENABLE_AFTER_ALARM, type = Type.OnEntry)
        public void onEntryStateWaitForEnableAfterAlarm() {
            reportState("state changed to STATE_WAIT_FOR_ENABLE_AFTER_ALARM: " + STATE_WAIT_FOR_ENABLE_AFTER_ALARM);
        }

        @StateHandler(state = STATE_CLOUD_AUTH, type = Type.OnEntry)
        public void onEntryStateAuthAfterAlarm() {
            reportState("state changed to STATE_CLOUD_AUTH: " + STATE_CLOUD_AUTH);
        }

        @StateHandler(state = STATE_CLOUD_DOWNLOAD, type = Type.OnEntry)
        public void onEntryStateCloudDownload() {
            reportState("state changed to STATE_CLOUD_DOWNLOAD: " + STATE_CLOUD_DOWNLOAD);
            actionOnCloudDownload();
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
        setTimeZone(am);
        setRepeatingAlarm(am, c);
    }

    private void setTimeZone(final AlarmManager am) {
        try {
            am.setTimeZone("Asia/Hong_Kong");
        } catch (Exception e) {
        }
    }

    private void setRepeatingAlarm(final AlarmManager am, final Calendar c) {
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), ALARM_REPEAT_INTERVAL_TIME, getAlarmPendingIntent());
    }

    private PendingIntent getAlarmPendingIntent() {
        Intent intent = new Intent(ALARM_INTENT_ACTION);
        PendingIntent startAlarmPendingIntent = PendingIntent.getBroadcast(RouterTestActivity.this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return startAlarmPendingIntent;
    }

    private void onReceivedAlarm() {
        if (tinyMachine.getCurrentState() == STATE_ALARM_SET) {
            ++testingCount;
            NetworkUtil.enableWiFi(this, false);
            tinyMachine.transitionTo(STATE_ALARM_TRIGGERED);
        }
    }

    private void reportAverageConnectTimeToCloud() {
        if (Float.compare(averageWifiConnectedDuration, 0) == 0) {
            averageWifiConnectedDuration = wifiConnectedDuration;
        } else {
            averageWifiConnectedDuration = (averageWifiConnectedDuration + wifiConnectedDuration) / 2;
        }
        reportApConnectDuration();
        connectDurationView.setText("平均连接时间: " + averageWifiConnectedDuration / 1000.0f + " 秒");
    }

    private void reportApSucceed() {
        Map<String, String> map = new HashMap<>();
        String address = NetworkUtil.getMacAddress(this);
        map.put("mac", address);
        MobclickAgent.onEvent(this, "apSucceed", map);
    }

    private void reportAuthSucceed() {
        Map<String, String> map = new HashMap<>();
        String address = NetworkUtil.getMacAddress(this);
        map.put("mac", address);
        MobclickAgent.onEvent(this, "authSucceed", map);
    }

    private void reportApConnectDuration() {
        Map<String, String> map = new HashMap<>();
        map.put("du", String.valueOf(averageWifiConnectedDuration));
        MobclickAgent.onEvent(this, "act", map);
    }

    private void reportDownloadResult(boolean succeed)  {
        Map<String, String> map = new HashMap<>();
        map.put("url", FileUtils.computeMD5(URL));

        if (succeed) {
            downloadSucceedCount ++;
            if (Float.compare(averageDownloadDuration, 0) == 0) {
                averageDownloadDuration = downloadDuration;
            } else {
                averageDownloadDuration = (averageDownloadDuration + downloadDuration) / 2;
            }
            map.put("okCount", String.valueOf(downloadSucceedCount));
            map.put("ts", String.valueOf(averageDownloadDuration));
            MobclickAgent.onEvent(this, "dok", map);
            downloadDurationView.setText("下载成功次数: " + downloadSucceedCount + " 下载时间: " + averageDownloadDuration / 1000.f +  " 秒");
        } else {
            ++downloadFailedCount;
            map.put("failCount", String.valueOf(downloadFailedCount));
            MobclickAgent.onEvent(this, "derr", map);
            downloadFailedView.setText("下载失败次数: " + downloadFailedCount);
        }
    }

    private void reportWifiFailed() {
        ++apFailedCount;
        Map<String, String> map = new HashMap<>();
        map.put("failed", String.valueOf(apFailedCount));
        MobclickAgent.onEvent(this, "apFail", map);
        apFailedView.setText("连接失败次数: " + apFailedCount);
    }

    private void reportState(final String text) {
        stateView.setText("测试总次数: " + testingCount + " " + text);
        Log.e(TAG, text);
    }
}
