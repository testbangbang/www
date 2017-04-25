package com.onyx.android.sdk.data.request.data.db;

import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * Created by ming on 2017/4/21.
 */

public class BackupRestoreDBRequest extends BaseDataRequest {

    private Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap;
    private boolean backup = false;

    public BackupRestoreDBRequest(Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap, boolean backup) {
        this.backupRestoreDBMap = backupRestoreDBMap;
        this.backup = backup;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (backupRestoreDBMap == null || backupRestoreDBMap.size() == 0) {
            return;
        }
        for (Map.Entry<DatabaseInfo, DatabaseInfo> entry : backupRestoreDBMap.entrySet()) {
            DatabaseInfo currentDB = entry.getKey();
            DatabaseInfo newDB = entry.getValue();
            if (!backup && !canRestoreDB(newDB.getDbPath(), currentDB.getVersion())) {
                continue;
            }
            transferDB(currentDB.getDbPath(), newDB.getDbPath(), backup);
        }
    }

    private boolean canRestoreDB(final String newDBPath, final int currentDBVersion) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(newDBPath, null,SQLiteDatabase.OPEN_READWRITE);
        int newDBVersion = database.getVersion();
        database.close();
        return currentDBVersion >= newDBVersion;
    }

    private void transferDB(final String currentDBPath, final String newDBPath, final boolean backup) throws Exception {
        File currentDB = new File(currentDBPath);
        File newDB = new File(newDBPath);

        FileChannel src;
        FileChannel dst;
        if (backup) {
            src = new FileInputStream(currentDB).getChannel();
            dst = new FileOutputStream(newDB).getChannel();
            dst.transferFrom(src, 0, src.size());
        }else {
            src = new FileOutputStream(currentDB).getChannel();
            dst = new FileInputStream(newDB).getChannel();
            dst.transferTo(0, dst.size(), src);
        }

        src.close();
        dst.close();
    }
}
