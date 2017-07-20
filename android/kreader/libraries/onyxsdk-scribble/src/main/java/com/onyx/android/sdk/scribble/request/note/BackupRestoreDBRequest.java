package com.onyx.android.sdk.scribble.request.note;

import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * Created by ming on 2017/4/21.
 */

public class BackupRestoreDBRequest extends BaseNoteRequest {

    private Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap;
    private boolean backup = false;

    public BackupRestoreDBRequest(Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap, boolean backup) {
        this.backupRestoreDBMap = backupRestoreDBMap;
        this.backup = backup;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        if (backupRestoreDBMap == null || backupRestoreDBMap.size() == 0) {
            return;
        }
        for (Map.Entry<DatabaseInfo, DatabaseInfo> entry : backupRestoreDBMap.entrySet()) {
            DatabaseInfo currentDB = entry.getKey();
            DatabaseInfo newDB = entry.getValue();
            if (!backup && !canRestoreDB(newDB.getDbPath(), currentDB.getVersion())) {
                continue;
            }
            if (backup) {
                FileUtils.transferFile(currentDB.getDbPath(), newDB.getDbPath());
            }else {
                FileUtils.transferFile(newDB.getDbPath(), currentDB.getDbPath());
            }
        }
    }

    private boolean canRestoreDB(final String newDBPath, final int currentDBVersion) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(newDBPath, null,SQLiteDatabase.OPEN_READWRITE);
        int newDBVersion = database.getVersion();
        database.close();
        return currentDBVersion >= newDBVersion;
    }
}
