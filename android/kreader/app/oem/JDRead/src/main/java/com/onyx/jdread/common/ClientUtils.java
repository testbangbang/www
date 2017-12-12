package com.onyx.jdread.common;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

import jd.wjlogin_sdk.common.WJLoginHelper;
import jd.wjlogin_sdk.model.ClientInfo;

public class ClientUtils {
    private static ClientInfo clientInfo;
    private static WJLoginHelper helper;

    public synchronized static ClientInfo getClientInfo() {
        if (null == clientInfo) {
            clientInfo = new ClientInfo();
            clientInfo.setDwAppID((short) 103);
            clientInfo.setAppName(JDReadApplication.getInstance().getResources().getString(R.string.app_name));
            clientInfo.setClientType("android");
            clientInfo.setUuid(AppInformationUtils.readDeviceUUID());
            clientInfo.setArea("SHA");
            clientInfo.setDwGetSig(1);
            clientInfo.setDwAppClientVer(AppInformationUtils.getAppVersionName());
            clientInfo.setOsVer(Build.VERSION.RELEASE);
            Display display = ((WindowManager) JDReadApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            if (null != display) {
                String screen = display.getWidth() + "*" + display.getHeight();
                clientInfo.setScreen(screen);
            }
            clientInfo.setDeviceBrand(splitSubString(Build.MANUFACTURER, 30).replaceAll(" ", ""));
            clientInfo.setDeviceModel(splitSubString((Build.MODEL), 30).replaceAll(" ", ""));
            clientInfo.setDeviceName(splitSubString((Build.PRODUCT), 30).replaceAll(" ", ""));
            clientInfo.setReserve(Build.VERSION.RELEASE);
            clientInfo.setDeviceId(getDeviceId());
            clientInfo.setSimSerialNumber(getSimSerialNumber());
        }

        return clientInfo;
    }

    private static String splitSubString(String value, int length) {
        try {
            if (value != null && value.length() > length) {
                value = value.substring(0, length);
            }
        } catch (Exception e) {

        }
        return value;
    }

    public static String getDeviceId() {
        try {
            TelephonyManager tm = (TelephonyManager) JDReadApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei == null ? "" : imei;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getSimSerialNumber() {
        try {
            TelephonyManager tm = (TelephonyManager) JDReadApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String simNo = tm.getSimSerialNumber();
            return simNo == null ? "" : simNo;
        } catch (Exception e) {
            return "";
        }
    }

    public static WJLoginHelper getWJLoginHelper() {
        if (null == helper) {
            synchronized (WJLoginHelper.class) {
                if (null == helper) {
                    helper = new WJLoginHelper(JDReadApplication.getInstance(), getClientInfo());
                    helper.setDevelop(WJLoginHelper.DEVELOP_TYPE.PRODUCT);
                    helper.createGuid();
                }
            }
        }

        return helper;
    }
}