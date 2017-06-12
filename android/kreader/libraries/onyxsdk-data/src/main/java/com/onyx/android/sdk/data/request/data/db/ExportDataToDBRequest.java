package com.onyx.android.sdk.data.request.data.db;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ShellUtils;

/**
 * Created by ming on 2017/6/2.
 */

public class ExportDataToDBRequest extends BaseDataRequest {

    private String command = "sqlite3 %s \"ATTACH '%s' AS CURRENT_DB; INSERT INTO " +
            "%s SELECT * FROM CURRENT_DB.%s " +
            "where CURRENT_DB.%s.%s; \"";
    private String currentDbPath;
    private String exportDbPath;
    private String condition;
    private String table;

    public ExportDataToDBRequest(String currentDbPath, String exportDbPath, String condition, String table) {
        this.currentDbPath = currentDbPath;
        this.exportDbPath = exportDbPath;
        this.condition = condition;
        this.table = table;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        command = String.format(command, exportDbPath , currentDbPath, table, table, table, condition);
        Debug.d(getClass(), "command: " + command);
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command, false);
        if (commandResult.result != 0) {
            throw new Exception(commandResult.errorMsg);
        }
    }
}
