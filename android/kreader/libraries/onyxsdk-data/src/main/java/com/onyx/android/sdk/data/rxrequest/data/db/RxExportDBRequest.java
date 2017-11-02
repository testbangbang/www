package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;

/**
 * Created by ming on 2017/5/22.
 */

public class RxExportDBRequest extends RxBaseDBRequest {
    private static final String TAG = "ExportDBRequest";

    private String command = "sqlite3 -csv %s \"%s \" > %s";
    private String currentDbPath;
    private String exportFilePath;
    private String condition;

    public RxExportDBRequest(DataManager dataManager, String currentDbPath, String exportFilePath, String condition) {
        super(dataManager);
        this.currentDbPath = currentDbPath;
        this.exportFilePath = exportFilePath;
        this.condition = condition;
    }

    @Override
    public RxExportDBRequest call() throws Exception {
        command = String.format(command, currentDbPath, condition, exportFilePath);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{"/system/bin/sh", "-c", command});
        if (process.waitFor() != 0) {
            throw new Exception("exit value = " + process.exitValue());
        }
        return this;
    }
}
