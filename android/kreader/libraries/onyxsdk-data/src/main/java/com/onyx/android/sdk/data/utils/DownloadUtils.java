package com.onyx.android.sdk.data.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.onyx.android.sdk.utils.FileUtils;

/**
 * Created by zhuzeng on 9/14/14.
 */
public class DownloadUtils {

    static final String SERVICE = Context.DOWNLOAD_SERVICE;
    static private final String TAG = DownloadUtils.class.getSimpleName();

    public static class DownloadCallback {
        public void stateChanged(int state, long finished, long total, long precentage) {

        }
    }

    static public DownloadManager getDownloadManager(Context context) {
        final String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(serviceString);
        return downloadManager;
    }

    static public long queryReferenceByUrl(final Context context, final String url) {
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = downloadManager.query(query);
        c.moveToFirst();
        long ref = -1;
        while (!c.isAfterLast()) {
            String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
            if (uri.equalsIgnoreCase(url)) {
                int s = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (s != DownloadManager.STATUS_FAILED) {
                    ref = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_ID));
                }
                break;
            }
            c.moveToNext();
        }
        c.close();
        return ref;
    }

    static public long download(Context context, final String url, final String localPath) {
        FileUtils.deleteFile(localPath);
        Uri uri = Uri.parse(url);
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);
        final String destination = String.format("file://%s", localPath);
        Uri dstUri = Uri.parse(destination);
        request.setDestinationUri(dstUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setVisibleInDownloadsUi(false);
        return getDownloadManager(context).enqueue(request);
    }

    static public void reportState(Context context, final long reference, final DownloadCallback callback) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(reference);
        Cursor c = getDownloadManager(context).query(query);
        int status;
        if(c.moveToFirst()) {
            status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.v(TAG, "STATUS_PAUSED");
                    break;
                case DownloadManager.STATUS_PENDING:
                    Log.v(TAG, "STATUS_PENDING");
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Log.v(TAG, "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.v(TAG, "Download finished");
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.v(TAG, "STATUS_FAILED reason: " + reason);
                    break;
            }

            long total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            long bytes = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            c.close();
            if (callback != null) {
                if (total > 0) {
                    callback.stateChanged(status, bytes, total, bytes * 100 / total);
                } else {
                    callback.stateChanged(status, bytes, total, 0);
                }
            }
        }
    }



}
