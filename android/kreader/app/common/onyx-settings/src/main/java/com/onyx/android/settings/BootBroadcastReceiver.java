package com.onyx.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.onyx.android.libsetting.util.QRCodeUtil;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;

import static com.onyx.android.libsetting.util.QRCodeUtil.CFA_QR_CODE_FILE_PATH;

public class BootBroadcastReceiver extends BroadcastReceiver {
    boolean restoreWifiStatus = false;
    WifiManager wifiManager;
    File cacheFile;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        cacheFile = new File(CFA_QR_CODE_FILE_PATH);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (!checkCacheQRFile()) {
                    try {
                        preEnableWifi(context);
                        cacheFile.getParentFile().mkdirs();
                        cacheFile.createNewFile();
                        BitmapUtils.saveBitmap(QRCodeUtil.stringToImageEncode(context, DeviceUtils.getDeviceMacAddress(context),
                                120, context.getResources().getColor(android.R.color.holo_blue_dark))
                                , cacheFile.getPath());
                        closeWifiIfNeeded();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean checkCacheQRFile() {
        return cacheFile.exists() && cacheFile.canRead();
    }

    private void preEnableWifi(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            return;
        }
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            restoreWifiStatus = true;
        }
        //TODO:boot up wifi enable use quite a long time could get mac address.so just sleep 3s here.
        try {
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeWifiIfNeeded() {
        if (restoreWifiStatus && wifiManager != null) {
            wifiManager.setWifiEnabled(false);
        }
    }
}

