
package com.onyx.android.dr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

/**
 * Created by zhouzhiming on 2017/8/12.
 */
public class RecorderReceiver extends BroadcastReceiver {
    private static final String TAG = "RecorderReceiver";
    public static final String FILES = "soundrecorder";
    public static final String CLEAR_FILE_ACTION = "com.onyx.android.dr.CLEAR_FILE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (CLEAR_FILE_ACTION.equals(action)) {
                clearFile();
         }
    }

    private void clearFile() {
        String[] files = {FILES};
        for (String str : files) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str);
            if (file.exists()) {
                deleteRecursive(file);
            }
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}
