package com.onyx.android.sdk.data.request.data.db;


import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.DatabaseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;


/**
 * Created by ming on 2017/4/21.
 */

public class TransferDBRequest extends BaseDataRequest {

    private String src;
    private String dst;
    private boolean restartDB = false;
    private boolean checkVersion = false;
    private Class<? extends DatabaseHolder> databaseHolderClass;

    public TransferDBRequest(String src, String dst, boolean restartDB, boolean checkVersion, Class<? extends DatabaseHolder> databaseHolderClass) {
        this.src = src;
        this.dst = dst;
        this.restartDB = restartDB;
        this.checkVersion = checkVersion;
        this.databaseHolderClass = databaseHolderClass;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (StringUtils.isNullOrEmpty(src) || StringUtils.isNullOrEmpty(dst)) {
            return;
        }
        if (checkVersion && !DatabaseUtils.canRestoreDB(src, dst)) {
            throw new Exception("Can not restore high version database");
        }
        FileUtils.transferFile(src, dst);
        if (!FileUtils.compareFileMd5(src, dst)) {
            throw new Exception("Md5 is not the same");
        }
        if (restartDB) {
            restartDB();
        }
    }

    private void restartDB() {
        if (databaseHolderClass == null) {
            return;
        }
        FlowManager.destroy();
        FlowConfig.Builder builder = new FlowConfig.Builder(getContext());
        builder.addDatabaseHolder(databaseHolderClass);
        FlowManager.init(builder.build());
    }
}
