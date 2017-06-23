package com.onyx.android.eschool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.WriterException;
import com.onyx.android.eschool.manager.LeanCloudManager;
import com.onyx.android.eschool.utils.QRCodeUtil;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.ShellUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final boolean DEBUG = false;
    static File cacheFile;
    DeviceBindQrCodeGenerateTask task;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        cacheFile = new File(QRCodeUtil.CFA_QR_CODE_FILE_PATH);
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
        task = new DeviceBindQrCodeGenerateTask(context);
    }


    static class DeviceBindQrCodeGenerateTask extends AsyncTask<Void, Integer, DeviceBind> {
        WeakReference<Context> contextWeakReference;

        DeviceBindQrCodeGenerateTask(Context cxt) {
            contextWeakReference = new WeakReference<>(cxt);
        }

        @Override
        protected DeviceBind doInBackground(Void... params) {
            Context context;
            if (contextWeakReference.get() == null) {
                return null;
            }
            context = contextWeakReference.get();
            DeviceBind deviceBind = null;
            try {
                cacheFile.getParentFile().mkdirs();
                cacheFile.createNewFile();
                deviceBind = createDeviceBind(context);
                Log.e("#################", "generate device qr code");
                BitmapUtils.saveBitmap(QRCodeUtil.stringToImageEncode(context, JSONObjectParseUtils.toJson(deviceBind),
                        QRCodeUtil.DEFAULT_SIZE, true, QRCodeUtil.WHITE_MARGIN_SIZE,
                        context.getResources().getColor(android.R.color.black))
                        , cacheFile.getPath());
                ShellUtils.execCommand("busybox chmod 644 " + cacheFile.getPath(), false);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return deviceBind;
        }

        @Override
        protected void onPostExecute(DeviceBind result) {
            if (DEBUG) {
                Log.e("QrCodeGenerateTask", "get device bind:" + JSONObjectParseUtils.toJson(result));
                Log.e("QrCodeGenerateTask", "get Mac address:" + result == null ? null : result.mac);
            }
        }
    }

    static private DeviceBind createDeviceBind(Context context) {
        DeviceBind deviceBind = new DeviceBind();
        deviceBind.mac = NetworkUtil.getMacAddress(context);
        deviceBind.installationId = LeanCloudManager.getInstallationId();
        deviceBind.model = Build.MODEL;
        return deviceBind;
    }

    private boolean checkCacheQRFile() {
        return cacheFile.exists() && cacheFile.canRead();
    }

}

