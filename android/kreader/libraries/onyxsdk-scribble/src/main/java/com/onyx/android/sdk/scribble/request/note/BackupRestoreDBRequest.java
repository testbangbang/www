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

    private DatabaseInfo currentDB;
    private DatabaseInfo newDB;
    private boolean backup = false;

    public BackupRestoreDBRequest(DatabaseInfo currentDB, DatabaseInfo newDB, boolean backup) {
        this.currentDB = currentDB;
        this.newDB = newDB;
        this.backup = backup;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        if (currentDB == null || newDB == null) {
            return;
        }
        if (!backup && !canRestoreDB(newDB.getDbPath(), currentDB.getVersion())) {
            return;
        }
        if (backup) {
            FileUtils.transferFile(currentDB.getDbPath(), newDB.getDbPath());
        }else {
            FileUtils.transferFile(newDB.getDbPath(), currentDB.getDbPath());
        }
    }

    private boolean canRestoreDB(final String newDBPath, final int currentDBVersion) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(newDBPath, null,SQLiteDatabase.OPEN_READWRITE);
        int newDBVersion = database.getVersion();
        database.close();
        return currentDBVersion >= newDBVersion;
    }
}
