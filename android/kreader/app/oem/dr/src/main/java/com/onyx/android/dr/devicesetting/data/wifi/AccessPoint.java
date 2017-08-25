package com.onyx.android.dr.devicesetting.data.wifi;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

import com.onyx.android.dr.devicesetting.manager.WifiAdmin;


/**
 * Created by solskjaer49 on 2016/12/1 19:27.
 */

public class AccessPoint extends BaseObservable {

    private WifiAdmin wifiAdmin;
    private ScanResult scanResult;
    private WifiConfiguration wifiConfiguration;
    private int security;
    private String securityString;
    private String securityMode;
    private int signalLevel;
    private int[] imageState;
    private WifiInfo wifiInfo;
    private String password;
    private NetworkInfo.DetailedState detailedState;
    private int disableReason = -1;

    public AccessPoint(ScanResult result, WifiAdmin admin) {
        this.scanResult = result;
        this.wifiAdmin = admin;
        setWifiConfiguration();
        setSecurity();
        setSecurityMode();
        updateSecurityString();
        setSignalLevel();
        setImageState();
        updateWifiInfo();
    }

    @Bindable
    public ScanResult getScanResult() {
        return scanResult;
    }

    private void setWifiConfiguration() {
        wifiConfiguration = wifiAdmin.getWifiConfiguration(scanResult);
        if (wifiConfiguration != null) {
            try {
                disableReason = wifiAdmin.getDisableReason(wifiConfiguration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Bindable
    public WifiConfiguration getWifiConfiguration() {
        return wifiConfiguration;
    }

    private void setSecurity() {
        security = wifiAdmin.getSecurity(scanResult);
    }

    @Bindable
    public int getSecurity() {
        return security;
    }

    private void setSecurityMode() {
        securityMode = wifiAdmin.getSecurityMode(scanResult, false, security);
    }

    @Bindable
    public String getSecurityMode() {
        return securityMode;
    }

    private void updateSecurityString() {
        securityString = wifiAdmin.getSecurityString(this);
    }

    public void setSecurityString(String string) {
        securityString = string;
    }

    @Bindable
    public String getSecurityString() {
        return securityString;
    }

    private void setSignalLevel() {
        signalLevel = wifiAdmin.getWifiSignalLevel(scanResult);
    }

    @Bindable
    public int getSignalLevel() {
        return signalLevel;
    }

    private void setImageState() {
        imageState = wifiAdmin.getWifiImageState(security);
    }

    @Bindable
    public int[] getImageState() {
        return imageState;
    }

    public void updateWifiInfo() {
        wifiInfo = wifiAdmin.getWifiInfo(scanResult);
    }

    @Bindable
    public WifiInfo getWifiInfo() {
        return wifiInfo;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public NetworkInfo.DetailedState getDetailedState() {
        return detailedState;
    }

    public void setDetailedState(NetworkInfo.DetailedState state) {
        this.detailedState = state;
        updateSecurityString();
    }

    @Bindable
    public int getDisableReason() {
        return disableReason;
    }
}
