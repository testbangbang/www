package com.onyx.kreader.ui.data;

import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by joy on 4/26/17.
 */

public class DrmCertificateFactory {
    public static String getDrmCertificate() {
        File drmFile = new File("/sdcard/public_key");
        if (!drmFile.exists() || !drmFile.isFile()) {
            return null;
        }
        return FileUtils.readContentOfFile(drmFile);
    }
}
