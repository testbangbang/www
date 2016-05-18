/**
 *
 */
package com.onyx.android.sdk.download;

import java.io.File;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author jim
 *
 */
public class ResourceDownloader {

    private static final String TAG = "ResourceDownloader";
    private static final String DEFAULT_DESTINATION_DIR = ".Z4m3uck2j1m";

    public interface OnDownloadCompleteListener {
        public void onDownloadComplete(String absoluteFilePath);
    }

    
    private Context context;
    private String httpLink;
    private String authorization;
    private OnDownloadCompleteListener downloadCompleteListener;

//    private DownloadManager mDownloadManager;
    private Uri mFileUri;
    private String resourceName;
    private DownloadCompleteReceiver mDownloadCompleteReceiver;
    private AsyncTask mDownLoadAsyncTask;
//    private DownloadManager.Request request = null;
    private String destinationDir;
    private String absolutePath;

    public ResourceDownloader(Context context, String httpLink, String authorization, OnDownloadCompleteListener listener) {
        this.context = context;
        this.httpLink = httpLink;
        this.authorization = authorization;
        this.downloadCompleteListener = listener;

        init();
    }
    
    private void init() {
//        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        try {
//            mDownloadCompleteReceiver = new DownloadCompleteReceiver();
//            context.registerReceiver(mDownloadCompleteReceiver,
//                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//
//            mFileUri = Uri.parse(httpLink);
//
//            resourceName = httpLink.substring(httpLink.lastIndexOf("/") + 1);
//
//            Log.d(TAG, "resourceName = " + resourceName);
//
//            destinationDir = EnvironmentUtil.getExternalStorageDirectory()
//                    + File.separator + DEFAULT_DESTINATION_DIR;
//            Log.d(TAG, "destinationDir = " + destinationDir);
//
//            absolutePath = destinationDir + File.separator + resourceName;
//
//            File resourceDirFile = new File(destinationDir);
//            if (!resourceDirFile.exists()) {
//                resourceDirFile.mkdirs();
//            }
//
//        } catch (Throwable tr) {
//            tr.printStackTrace();
//            if (mDownloadCompleteReceiver != null) {
//                context.unregisterReceiver(mDownloadCompleteReceiver);
//            }
//        }
    }

    public void cleanDownloadCompleteListener() {
        downloadCompleteListener = null;
    }
    
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(
//                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//                Log.d(TAG, "DownLoadComplete, abso path is " + absolutePath);
//                if (downloadCompleteListener != null) {
//                    downloadCompleteListener.onDownloadComplete(absolutePath);
//                }
//            }
        }
    }

    public DownloadManager.Request getRequest() {
//        if (request == null) {
//            request = new DownloadManager.Request(mFileUri);
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//        }

        return null;
    }

    public long start() {
//        request = getRequest();
//        request.addRequestHeader(HttpHelper.REQ_KEY_AUTHORIZATION, authorization);
//
//        Log.d(TAG, "AbsolutePath = " + ("file://" + absolutePath));
//        deleteFiles();
//        request.setDestinationUri(Uri.parse("file://" + absolutePath));
        return 1l;
    }

    public File getFile() {
        return new File(absolutePath);
    }

    public boolean delete() {
       File dir = new File(destinationDir);
       File[] files = dir.listFiles();
       for(File file : files) {
           if (!file.delete()) {
               Log.d(TAG, "Delete file failed");
               return false;
           }
       }
       return dir.delete();
    }
    
    public boolean deleteFiles() {
        File dir = new File(destinationDir);
        File[] files = dir.listFiles();
        for(File file : files) {
            if (!file.delete()) {
                Log.d(TAG, "Delete file failed");
                return false;
            }
        }
        return true;
     }
}
