package com.onyx.edu.reader.ui.data;

import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by joy on 4/26/17.
 */

public class DrmCertificateFactory implements ReaderDrmCertificateFactory {

    public String getDrmCertificate() {
        File drmFile = new File("/sdcard/public_key");
        if (!drmFile.exists() || !drmFile.isFile()) {
            return null;
        }
        return FileUtils.readContentOfFile(drmFile);
    }
}
