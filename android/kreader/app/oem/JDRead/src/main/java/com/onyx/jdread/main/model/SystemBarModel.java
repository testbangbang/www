package com.onyx.jdread.main.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;

import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.event.SystemBarClickedEvent;
import com.onyx.jdread.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Observable;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class SystemBarModel extends Observable {
    private static final int[] WIFI_SIGNAL_STRENGTH = {
            R.drawable.ic_wifi_1,
            R.drawable.ic_wifi_2,
            R.drawable.ic_wifi_3,
            R.drawable.ic_wifi_4};

    private static final int[][] BATTERY_SIGNAL_STRENGTH = {
            {R.drawable.ic_battery_0,
                    R.drawable.ic_battery_10,
                    R.drawable.ic_battery_20,
                    R.drawable.ic_battery_30,
                    R.drawable.ic_battery_40,
                    R.drawable.ic_battery_50,
                    R.drawable.ic_battery_60,
                    R.drawable.ic_battery_70,
                    R.drawable.ic_battery_80,
                    R.drawable.ic_battery_90,
                    R.drawable.ic_battery_100},
            {R.drawable.ic_battery_charge_0,
                    R.drawable.ic_battery_charge_10,
                    R.drawable.ic_battery_charge_20,
                    R.drawable.ic_battery_charge_30,
                    R.drawable.ic_battery_charge_40,
                    R.drawable.ic_battery_charge_50,
                    R.drawable.ic_battery_charge_60,
                    R.drawable.ic_battery_charge_70,
                    R.drawable.ic_battery_charge_80,
                    R.drawable.ic_battery_charge_90,
                    R.drawable.ic_battery_charge_100}
    };
    public ObservableBoolean systemBarClickEnable = new ObservableBoolean(true);
    private static final int WIFI_LEVEL_COUNT = WIFI_SIGNAL_STRENGTH.length;
    private int level;

    private IntentFilter batteryIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        return intentFilter;
    }

    private IntentFilter wifiIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return intentFilter;
    }

    private ObservableBoolean isShow = new ObservableBoolean(true);

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public final ObservableInt wifiImageRes = new ObservableInt(R.drawable.ic_qs_wifi);
    public final ObservableInt batteryImageRes = new ObservableInt();
    public final ObservableField<String> battery = new ObservableField<>();
    public final ObservableField<String> time = new ObservableField<>();

    public SystemBarModel() {
        updateTime();
    }

    public void updateTime() {
        String time = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DEFAULT_TIME_FORMAT);
        this.time.set(time);
    }

    public void setTimeFormat(boolean is24Hour) {
        TimeUtils.setFormat(new SimpleDateFormat(is24Hour ? TimeUtils.DATA_TIME_24 : TimeUtils.DATA_TIME_12_WITH_AMPM));
    }

    private BroadcastReceiver phoneBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                boolean connectionStatus = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
                int connectCondition = (connectionStatus ? 1 : 0);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                int levels = scale / BATTERY_SIGNAL_STRENGTH[connectCondition].length;
                int interval = 100 / levels;
                batteryImageRes.set(BATTERY_SIGNAL_STRENGTH[connectCondition][level / interval]);
                int power = level * 100 / scale;
                battery.set(power + "%");
            }
        }
    };

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (NetworkUtil.isWifiEnabled(context)) {
                    wifiImageRes.set(NetworkUtil.isWiFiConnected(context) ? WIFI_SIGNAL_STRENGTH[level] : R.drawable.ic_qs_wifi);
                }
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (NetworkUtil.isWifiEnabled(context)) {
                    wifiImageRes.set(NetworkUtil.isWiFiConnected(context) ? WIFI_SIGNAL_STRENGTH[level] : R.drawable.ic_qs_wifi);
                } else {
                    wifiImageRes.set(0);
                }
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
                level = WifiManager.calculateSignalLevel(wifiRssi, WIFI_LEVEL_COUNT);
                wifiImageRes.set(WIFI_SIGNAL_STRENGTH[level]);
            }
        }
    };

    public void registerReceiver(Context context) {
        context.registerReceiver(phoneBatteryReceiver, batteryIntentFilter());
        context.registerReceiver(wifiReceiver, wifiIntentFilter());
    }

    public void unRegisterReceiver(Context context) {
        context.unregisterReceiver(phoneBatteryReceiver);
        context.unregisterReceiver(wifiReceiver);
    }

    public void toggleWifi() {
        NetworkUtil.toggleWiFi(JDReadApplication.getInstance());
    }

    public void onClicked() {
        EventBus.getDefault().post(new SystemBarClickedEvent());
    }
}
