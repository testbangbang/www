package com.onyx.android.sdk.reader.tests;

import android.content.Context;

import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

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
            return NetworkUtil.getMacAddress(context);
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
