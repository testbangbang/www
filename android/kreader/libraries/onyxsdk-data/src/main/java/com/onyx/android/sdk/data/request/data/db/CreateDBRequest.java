package com.onyx.android.sdk.data.request.data.db;

import android.os.Environment;
import android.util.Log;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.ShellUtils;

/**
 * Created by ming on 2017/6/1.
 */

public class CreateDBRequest extends BaseDataRequest {

    private String exportCommand = "sqlite3 %s .schema > %s";
    private String importCommand = "sqlite3 %s < %s";

    private String currentDbPath;
    private String createDbPath;
    private String schemaFilePath;

    public CreateDBRequest(String currentDbPath, String createDbPath) {
        this.currentDbPath = currentDbPath;
        this.createDbPath = createDbPath;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        schemaFilePath = Environment.getExternalStorageDirectory().getPath() + "/" +  Constant.READER_DATA_FOLDER + "/schema.sql";
        FileUtils.deleteFile(schemaFilePath);
        FileUtils.ensureFileExists(schemaFilePath);
        exportCommand = String.format(exportCommand, currentDbPath, schemaFilePath);
        importCommand = String.format(importCommand, createDbPath, schemaFilePath);
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(false, exportCommand, importCommand);
        if (commandResult.result != 0) {
            throw new Exception(commandResult.errorMsg);
        }
    }
}
