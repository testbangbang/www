package com.onyx.android.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.onyx.android.sdk.utils.FileUtils;

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
                    for (File file : collectAllSubFile(dbFolder)) {
                        file.setReadable(true, false);
                        file.setExecutable(true, false);
                    }
                    break;
                case LOCK_DB_FILE_PERMISSION_ACTION:
                    for (File file : collectAllSubFile(dbFolder)) {
                        file.setReadable(true);
                        file.setExecutable(false, false);
                        file.setExecutable(true);
                    }
                    break;
            }
        }
    }

    private List<File> collectAllSubFile(File folder) {
        List<File> resultList = new ArrayList<>();
        resultList.add(folder);
        FileUtils.getAllFileUnderCurrentFolder(folder.getPath(), null, true, resultList);
        return resultList;
    }
}
