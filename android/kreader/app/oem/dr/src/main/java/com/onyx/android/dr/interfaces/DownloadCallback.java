package com.onyx.android.dr.interfaces;

/**
 * Created by 12 on 2017/1/17.
 */

public interface DownloadCallback {
    void progressChanged(final int reference,
                         final String title,
                         final String remoteUri,
                         final String localUri,
                         int state,
                         long finished,
                         long total,
                         long percentage);
}
