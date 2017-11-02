package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ShellUtils;

/**
 * Created by ming on 2017/6/2.
 */

public class RxExportDataToDBRequest extends RxBaseDBRequest {

    private String command = "sqlite3 %s \"ATTACH '%s' AS CURRENT_DB; INSERT INTO " +
            "%s SELECT * FROM CURRENT_DB.%s " +
            "where CURRENT_DB.%s.%s; \"";
    private String currentDbPath;
    private String exportDbPath;
    private String condition;
    private String table;

    public RxExportDataToDBRequest(DataManager dataManager,String currentDbPath, String exportDbPath, String condition, String table) {
        super(dataManager);
        this.currentDbPath = currentDbPath;
        this.exportDbPath = exportDbPath;
        this.condition = condition;
        this.table = table;
    }

    @Override
    public RxExportDataToDBRequest call() throws Exception {
        command = String.format(command, exportDbPath , currentDbPath, table, table, table, condition);
        Debug.d(getClass(), "command: " + command);
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command, false);
        if (commandResult.result != 0) {
            throw new Exception(commandResult.errorMsg);
        }
        return this;
    }
}
