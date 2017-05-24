package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by ming on 2017/5/22.
 */

public class ImportDBRequest extends BaseDataRequest {

    private String command = "sqlite3 -separator \",\" %s \".import %s %s\"";
    private String currentDbPath;
    private String importFilePath;
    private String table;

    public ImportDBRequest(String currentDbPath, String importFilePath, String table) {
        this.currentDbPath = currentDbPath;
        this.importFilePath = importFilePath;
        this.table = table;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        command = String.format(command, currentDbPath, importFilePath, table);
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(new String[] {"/system/bin/sh", "-c",  command});
        if (proc.waitFor() != 0) {
            throw new Exception("exit value = " + proc.exitValue());
        }
    }
}
