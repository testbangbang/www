package com.onyx.android.sdk.data.rxrequest.data.db;

import android.os.Environment;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.ShellUtils;

/**
 * Created by hehai on 17-11-1.
 */

public class RxCreateDBRequest extends RxBaseDBRequest {
    private String exportCommand = "sqlite3 %s .schema > %s";
    private String importCommand = "sqlite3 %s < %s";

    private String currentDbPath;
    private String createDbPath;
    private String schemaFilePath;

    public RxCreateDBRequest(DataManager dm, String currentDbPath, String createDbPath) {
        super(dm);
        this.currentDbPath = currentDbPath;
        this.createDbPath = createDbPath;
    }

    @Override
    public RxCreateDBRequest call() throws Exception {
        schemaFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.READER_DATA_FOLDER + "/schema.sql";
        FileUtils.deleteFile(schemaFilePath);
        FileUtils.ensureFileExists(schemaFilePath);
        FileUtils.deleteFile(createDbPath);
        FileUtils.ensureFileExists(createDbPath);
        exportCommand = String.format(exportCommand, currentDbPath, schemaFilePath);
        importCommand = String.format(importCommand, createDbPath, schemaFilePath);
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(false, exportCommand, importCommand);
        if (commandResult.result != 0) {
            throw new Exception(commandResult.errorMsg);
        }
        return this;
    }
}
