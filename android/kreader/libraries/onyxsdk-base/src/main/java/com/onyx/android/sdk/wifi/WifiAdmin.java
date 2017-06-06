package com.onyx.android.sdk.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.onyx.android.sdk.R;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static com.onyx.android.sdk.wifi.PskType.WPA;
import static com.onyx.android.sdk.wifi.PskType.WPA2;
import static com.onyx.android.sdk.wifi.PskType.WPA_WPA2;


/**
 * Created by solskjaer49 on 2016/12/1 16:27.
 */

public class WifiAdmin {
    private static final String TAG = WifiAdmin.class.getSimpleName();
    private WifiManager wifiManager;
    private List<ScanResult> wifiScanResultList;
    private Context context;
    private IntentFilter wifiStateFilter;
    private Callback callback;
    private BroadcastReceiver wifiStateReceiver;

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    public static final int DISABLED_UNKNOWN_REASON = 0;
    public static final int DISABLED_DNS_FAILURE = 1;
    public static final int DISABLED_DHCP_FAILURE = 2;
    public static final int DISABLED_AUTH_FAILURE = 3;

    private static final int[] STATE_SECURED = {
            R.attr.state_encrypted
    };

    private static final int[] STATE_NONE = {};

    public WifiAdmin setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback {
        void onWifiStateChange(boolean isWifiEnable);

        void onScanResultReady(List<AccessPoint> scanResult);

        void onSupplicantStateChanged(NetworkInfo.DetailedState state);

        void onNetworkConnectionChange(NetworkInfo.DetailedState state);
    }

    public WifiAdmin(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initWifiStateFilterAndReceiver();
    }

    public WifiAdmin(Context context, Callback callback) {
        this(context);
        setCallback(callback);
    }

    private void initWifiStateFilterAndReceiver() {
        wifiStateFilter = new IntentFilter();
        wifiStateFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiStateFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiStateFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifiStateFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case WifiManager.WIFI_STATE_CHANGED_ACTION:
                        callback.onWifiStateChange(wifiManager.isWifiEnabled());
                        if (wifiManager.isWifiEnabled()) {
                            triggerWifiScan();
                        }
                        break;
                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                        callback.onScanResultReady(buildResultList(wifiManager.getScanResults()));
                        break;
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf((SupplicantState)
                                intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                        callback.onSupplicantStateChanged(state);
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        callback.onNetworkConnectionChange(info.getDetailedState());
                        break;
                }
            }
        };
    }

    private List<AccessPoint> buildResultList(Collection<ScanResult> scanResults) {
        List<AccessPoint> resultList = new LinkedList<>();
        AccessPoint connectedPoint = null;
        for (ScanResult item : scanResults) {
            AccessPoint point = new AccessPoint(item, this);
            if (point != null && point.getWifiConfiguration() != null) {
                if (Debug.getDebug()) {
                    Log.e(TAG, point.getWifiConfiguration().SSID + "(networkID):" + point.getWifiConfiguration().networkId);
                }
            }
            if (getCurrentConnectionInfo() != null && point.getWifiConfiguration() != null) {
                if (point.getWifiConfiguration().networkId == getCurrentConnectionInfo().getNetworkId()) {
                    point.updateWifiInfo();
                    point.setDetailedState(NetworkInfo.DetailedState.CONNECTED);
                    point.setSecurityString(context.getString(R.string.wifi_connected));
                    connectedPoint = point;
                }
            }
            resultList.add(point);
        }
        Collections.sort(resultList, new Comparator<AccessPoint>() {
            @Override
            public int compare(AccessPoint a1, AccessPoint a2) {
                return a2.getSignalLevel() - a1.getSignalLevel();
            }
        });
        if (connectedPoint != null) {
            resultList.remove(connectedPoint);
            resultList.add(0, connectedPoint);
        }
        return resultList;
    }

    public boolean registerReceiver() {
        if (context == null) {
            return false;
        }
        context.registerReceiver(wifiStateReceiver, wifiStateFilter);
        return true;
    }

    public boolean unregisterReceiver() {
        if (context == null) {
            return false;
        }
        context.unregisterReceiver(wifiStateReceiver);
        return true;
    }

    public void toggleWifi() {
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public void setWifiEnabled(boolean isWifiEnabled){
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(isWifiEnabled);
        }
    }

    public void triggerWifiScan() {
        if (wifiManager != null) {
            wifiManager.startScan();
        }
    }

    public WifiInfo getCurrentConnectionInfo() {
        return wifiManager.getConnectionInfo();
    }

    public int checkWifiState() {
        return wifiManager.getWifiState();
    }

    public WifiConfiguration getWifiConfiguration(ScanResult result) {
        WifiConfiguration wifiConfiguration = null;
        if (wifiManager.getConfiguredNetworks() == null) {
            return null;
        }
        for (WifiConfiguration configuration : wifiManager.getConfiguredNetworks()) {
            if (WifiUtil.isSameSSID(configuration.SSID, result.SSID)) {
                wifiConfiguration = configuration;
                break;
            }
        }
        return wifiConfiguration;
    }

    public int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public String getSecurityString(AccessPoint accessPoint) {
        ScanResult result = accessPoint.getScanResult();
        NetworkInfo.DetailedState state = accessPoint.getDetailedState();
        int security = accessPoint.getSecurity();
        WifiConfiguration configuration = accessPoint.getWifiConfiguration();
        String securityMode = accessPoint.getSecurityMode();
        boolean wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
        StringBuilder summary = new StringBuilder();
        if (state != null) {
            return getDetailedState(state, result.SSID);
        } else if (configuration != null && configuration.status == WifiConfiguration.Status.DISABLED) {
            switch (accessPoint.getDisableReason()) {
                case DISABLED_AUTH_FAILURE:
                    return context.getString(R.string.wifi_disabled_password_failure);
                case DISABLED_DHCP_FAILURE:
                case DISABLED_DNS_FAILURE:
                    return context.getString(R.string.wifi_disabled_network_failure);
                case DISABLED_UNKNOWN_REASON:
                    return context.getString(R.string.wifi_disabled_generic);
            }
        } else {
            if (configuration != null) { // Is saved network
                summary.append(context.getString(R.string.wifi_remembered));
            }

            if (security != SECURITY_NONE) {
                String securityStrFormat;
                if (summary.length() == 0) {
                    securityStrFormat = context.getString(R.string.wifi_secured_first_item);
                } else {
                    securityStrFormat = context.getString(R.string.wifi_secured_second_item);
                }
                summary.append(String.format(securityStrFormat, securityMode));
            }

            if (configuration == null && wpsAvailable) { // Only list WPS available for unsaved networks
                if (summary.length() == 0) {
                    summary.append(context.getString(R.string.wifi_wps_available_first_item));
                } else {
                    summary.append(context.getString(R.string.wifi_wps_available_second_item));
                }
            }
        }
        return summary.toString();
    }

    public String getSecurityMode(ScanResult result, boolean concise, int security) {
        switch (security) {
            case SECURITY_EAP:
                return concise ? context.getString(R.string.wifi_security_short_eap) :
                        context.getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (getPskType(result)) {
                    case WPA:
                        return concise ? context.getString(R.string.wifi_security_short_wpa) :
                                context.getString(R.string.wifi_security_wpa);
                    case WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa2) :
                                context.getString(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) :
                                context.getString(R.string.wifi_security_wpa_wpa2);
                    case PskType.UNKNOWN:
                    default:
                        return concise ? context.getString(R.string.wifi_security_short_psk_generic)
                                : context.getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return concise ? context.getString(R.string.wifi_security_short_wep) :
                        context.getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return concise ? "" : context.getString(R.string.wifi_security_none);
        }
    }

    private
    @PskType.PskTypeDef
    int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return WPA_WPA2;
        } else if (wpa2) {
            return WPA2;
        } else if (wpa) {
            return WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    public WifiInfo getWifiInfo(ScanResult result) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }
        String ssid = wifiInfo.getSSID();
        if (StringUtils.isNullOrEmpty(ssid)) {
            return null;
        }

        /*
          After API 17,getSSID() will return ssid with quotation mark.
          ref link:https://developer.android.com/reference/android/net/wifi/WifiInfo.html#getSSID()
        */
        if (WifiUtil.isSameSSID(ssid, result.SSID)) {
            return wifiInfo;
        }
        return null;
    }

    public int getWifiSignalLevel(ScanResult result) {
        return WifiManager.calculateSignalLevel(result.level, 4);
    }

    public int[] getWifiImageState(int security) {
        return (security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE;
    }

    public void connectWifi(AccessPoint accessPoint) {
        WifiConfiguration configuration = accessPoint.getWifiConfiguration();
        int networkId;
        if (configuration != null) {
            networkId = configuration.networkId;
        } else {
            configuration = createWifiConfiguration(accessPoint);
            networkId = wifiManager.addNetwork(configuration);
        }
        boolean success = wifiManager.enableNetwork(networkId, true);
        if (success) {
            wifiManager.saveConfiguration();
        }
    }

    public WifiConfiguration createWifiConfiguration(AccessPoint accessPoint) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + accessPoint.getScanResult().SSID + "\"";
        String password = accessPoint.getPassword();
        switch (accessPoint.getSecurity()) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int length = password.length();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58) &&
                        password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = '"' + password + '"';
                }
                break;
            case SECURITY_EAP:
                //nothing to do
                break;
            default:
                return null;
        }
        return config;
    }

    public String getSignalString(int signal) {
        return context.getResources().getStringArray(R.array.wifi_signal)[signal];
    }

    public boolean forget(AccessPoint accessPoint) {
        wifiManager.removeNetwork(accessPoint.getWifiConfiguration().networkId);
        return wifiManager.saveConfiguration();
    }

    public String getLocalIPAddress() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "";
    }

    private String getDetailedState(NetworkInfo.DetailedState state, String ssid) {
        String[] formats = context.getResources().getStringArray((ssid == null)
                ? R.array.wifi_status : R.array.wifi_status_with_ssid);
        int index = state.ordinal();
        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index], ssid);
    }

    public int getPosition(List<AccessPoint> wifiList, NetworkInfo.DetailedState state, WifiInfo connectionInfo) {
        if (wifiList == null) {
            return -1;
        }
        int position = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            AccessPoint accessPoint = wifiList.get(i);
            WifiConfiguration config = accessPoint.getWifiConfiguration();
            if (config == null) {
                continue;
            }
            int networkId = config.networkId;
            if (connectionInfo != null && networkId != -1
                    && networkId == connectionInfo.getNetworkId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getDisableReason(WifiConfiguration config) throws Exception {
        Class ownerClass = config.getClass();
        Field field = ownerClass.getField("disableReason");
        Object property = field.get(config);
        return (int) property;
    }

}