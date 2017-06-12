package com.onyx.edu.reader.ui.data;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by joy on 4/26/17.
 */

public class DrmCertificateFactory implements ReaderDrmCertificateFactory {

    private Context context;

    public DrmCertificateFactory(Context context) {
        this.context = context;
    }

    @Override
    public String getDeviceId() {
        try {
            WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String address = info.getMacAddress().toLowerCase();
            return address;
        } catch (Throwable tr) {
            return "";
        }
    }

    public String getDrmCertificate() {
        File drmFile = new File("/sdcard/public_key");
        if (!drmFile.exists() || !drmFile.isFile()) {
            return null;
        }
        return FileUtils.readContentOfFile(drmFile);
    }
}
