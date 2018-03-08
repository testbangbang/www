package com.onyx.android.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChangeDBFilePermissionForDiagnosticReceiver extends BroadcastReceiver {
    private static final String TAG = ChangeDBFilePermissionForDiagnosticReceiver.class.getSimpleName();
    private static final String UNLOCK_DB_FILE_PERMISSION_ACTION = "onyx.android.note.intent.action.UNLOCK_DB_FILE_PERMISSION";
    private static final String LOCK_DB_FILE_PERMISSION_ACTION = "onyx.android.note.intent.action.LOCK_DB_FILE_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        File dbFolder = new File(context.getFilesDir().getParentFile().getPath() + File.separator + "databases");
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case UNLOCK_DB_FILE_PERMISSION_ACTION:
                    for (File file : getAllFileUnderCurrentFolder(dbFolder)) {
                        file.setReadable(true, false);
                        file.setExecutable(true, false);
                    }
                    break;
                case LOCK_DB_FILE_PERMISSION_ACTION:
                    for (File file : getAllFileUnderCurrentFolder(dbFolder)) {
                        file.setReadable(true);
                        file.setExecutable(false, false);
                        file.setExecutable(true);
                    }
                    break;
            }
        }
    }

    private List<File> getAllFileUnderCurrentFolder(File folder) {
        List<File> resultList = new ArrayList<>();
        File[] folderSubItems = folder.listFiles();
        resultList.add(folder);
        if (folderSubItems != null) {
            for (File subFile : folderSubItems) {
                if (subFile.isDirectory()) {
                    resultList.addAll(getAllFileUnderCurrentFolder(subFile));
                } else {
                    resultList.add(subFile);
                }
            }
        }
        return resultList;
    }
}
