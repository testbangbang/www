package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.transaction.ProcessDeleteModel;
import com.onyx.android.sdk.data.transaction.ProcessSaveModel;
import com.onyx.android.sdk.data.transaction.ProcessUpdateModel;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import java.util.List;

/**
 * Created by suicheng on 2016/8/12.
 */
public class StoreUtils {

    public static final int PROCESS_SAVE = 0;
    public static final int PROCESS_DELETE = 1;
    public static final int PROCESS_UPDATE = 2;

    static public <T extends BaseData> ProcessModelTransaction.ProcessModel<T> getProcessModel(final int process) {
        ProcessModelTransaction.ProcessModel<T> processModel;
        switch (process) {
            case PROCESS_SAVE:
                processModel = new ProcessSaveModel<>();
                break;
            case PROCESS_UPDATE:
                processModel = new ProcessUpdateModel<>();
                break;
            case PROCESS_DELETE:
                processModel = new ProcessDeleteModel<>();
                break;
            default:
                processModel = new ProcessSaveModel<>();
                break;
        }
        return processModel;
    }

    static public <T extends BaseData> void processOnAsync(final ProductResult<T> productResult, final int process,
                                                             ProcessModelTransaction.OnModelProcessListener<T> processListener) {
        ProcessModelTransaction.ProcessModel<T> processModel = getProcessModel(process);
        ProcessModelTransaction<T> processModelTransaction = new ProcessModelTransaction
                .Builder<>(processModel)
                .processListener(processListener)
                .addAll(productResult.list)
                .build();
        Transaction transaction = FlowManager.getDatabase(OnyxCloudDatabase.class)
                .beginTransactionAsync(processModelTransaction).build();
        transaction.execute();
    }

    static public <T extends BaseData> void saveToLocal(final ProductResult<T> productResult, final Class<T> clazz,
                                                          boolean clearBeforeSave) {
        if (clearTable(productResult, clazz, clearBeforeSave)) {
            processOnAsync(productResult, PROCESS_SAVE, null);
        }
    }

    static public <T extends BaseData> void saveToLocalFast(final ProductResult<T> productResult, final Class<T> clazz,
                                                              boolean clearBeforeSave) {
        if (clearTable(productResult, clazz, clearBeforeSave)) {
            saveToLocalFast(productResult.list, clazz);
        }
    }

    static public <T extends BaseData> void saveToLocalFast(List<T> list, final Class<T> clazz) {
        executeFastBuilder(FastStoreModelTransaction.saveBuilder(FlowManager.getModelAdapter(clazz)), list);
    }

    static public <T extends BaseData> void updateToLocalFast(List<T> list, final Class<T> clazz) {
        executeFastBuilder(FastStoreModelTransaction.updateBuilder(FlowManager.getModelAdapter(clazz)), list);
    }

    static private <T extends BaseData> void executeFastBuilder(FastStoreModelTransaction.Builder<T> builder, List<T> list) {
        builder.addAll(list).build().execute(FlowManager.getWritableDatabase(OnyxCloudDatabase.class));
    }

    static public <T extends BaseData> boolean clearTable(final ProductResult<T> productResult, final Class<T> clazz,
                                                            boolean clearBeforeSave) {
        if (CloudUtils.isEmpty(productResult)) {
            return false;
        }
        if (clearBeforeSave) {
            clearTable(clazz);
        }
        return true;
    }

    static public <T extends BaseModel> void clearTable(final Class<T> clazz) {
        SQLite.delete(clazz).execute();
    }

    static public <T extends BaseModel> void clearAllTable() {
        clearTable(Product.class);
        clearTable(Dictionary.class);
        clearTable(Category.class);
        clearTable(DownloadLink.class);
    }

    static public <T extends BaseModel> List<T> queryDataList(final Class<T> clazz, int limit) {
        return SQLite.select().from(clazz).limit(limit).queryList();
    }

    static public <T extends BaseModel> List<T> queryDataList(final Class<T> clazz) {
        return SQLite.select().from(clazz).queryList();
    }

    static public <T extends BaseModel> T queryDataSingle(final Class<T> clazz) {
        return SQLite.select().from(clazz).querySingle();
    }
}
