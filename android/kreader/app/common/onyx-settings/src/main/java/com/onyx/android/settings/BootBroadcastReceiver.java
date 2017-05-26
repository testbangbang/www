package com.onyx.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.WriterException;
import com.onyx.android.libsetting.util.QRCodeUtil;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.onyx.android.libsetting.util.QRCodeUtil.CFA_QR_CODE_FILE_PATH;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final boolean DEBUG = false;
    static boolean restoreWifiStatus = false;
    static WifiManager wifiManager;
    static File cacheFile;
    LoadMacAddressTask task;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        cacheFile = new File(CFA_QR_CODE_FILE_PATH);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (!checkCacheQRFile()) {
                    buildTask(context);
                    task.execute();
                }
            }
        }
    }

    private void buildTask(Context context) {
        if (task != null) {
            return;
        }
        task = new LoadMacAddressTask(context);
    }


    static class LoadMacAddressTask extends AsyncTask<Void, Integer, String> {
        WeakReference<Context> contextWeakReference;

        LoadMacAddressTask(Context cxt) {
            contextWeakReference = new WeakReference<>(cxt);
        }

        @Override
        protected String doInBackground(Void... params) {
            Context context;
            if (contextWeakReference.get() == null) {
                return null;
            }
            context = contextWeakReference.get();
            String macAddress = null;
            try {
                preEnableWifi(context);
                cacheFile.getParentFile().mkdirs();
                cacheFile.createNewFile();
                macAddress = DeviceUtils.getDeviceMacAddress(context);
                BitmapUtils.saveBitmap(QRCodeUtil.stringToImageEncode(context, macAddress,
                        120, context.getResources().getColor(android.R.color.holo_blue_dark))
                        , cacheFile.getPath());
                closeWifiIfNeeded();
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return macAddress;
        }

        @Override
        protected void onPostExecute(String result) {
            if (DEBUG) {
                Log.e("LoadMacAddressTask", "get Mac address:" + result);
            }
        }
    }

    private boolean checkCacheQRFile() {
        return cacheFile.exists() && cacheFile.canRead();
    }

    static private void preEnableWifi(Context context) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeWifiIfNeeded() {
        if (restoreWifiStatus && wifiManager != null) {
            wifiManager.setWifiEnabled(false);
        }
    }
}

