package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by ming on 2017/5/22.
 */

public class ExportDBRequest extends BaseDataRequest {
    private static final String TAG = "ExportDBRequest";

    private String command = "sqlite3 -csv %s \"%s \" > %s";
    private String currentDbPath;
    private String exportFilePath;
    private String condition;

    public ExportDBRequest(String currentDbPath, String exportFilePath, String condition) {
        this.currentDbPath = currentDbPath;
        this.exportFilePath = exportFilePath;
        this.condition = condition;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        command = String.format(command, currentDbPath, condition, exportFilePath);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[] {"/system/bin/sh", "-c",  command});
        if (process.waitFor() != 0) {
            throw new Exception("exit value = " + process.exitValue());
        }
    }
}
