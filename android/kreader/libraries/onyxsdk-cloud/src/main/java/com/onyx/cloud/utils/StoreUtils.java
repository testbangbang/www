package com.onyx.cloud.utils;

import com.onyx.cloud.OnyxCloudDatabase;
import com.onyx.cloud.db.transaction.ProcessDeleteModel;
import com.onyx.cloud.db.transaction.ProcessSaveModel;
import com.onyx.cloud.db.transaction.ProcessUpdateModel;
import com.onyx.cloud.model.BaseObject;
import com.onyx.cloud.model.Dictionary;
import com.onyx.cloud.model.DownloadLink;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductContainer;
import com.onyx.cloud.model.ProductResult;
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

    static public <T extends BaseObject> ProcessModelTransaction.ProcessModel<T> getProcessModel(final int process) {
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

    static public <T extends BaseObject> void processOnAsync(final ProductResult<T> productResult, final int process,
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

    static public <T extends BaseObject> void saveToLocal(final ProductResult<T> productResult, final Class<T> clazz,
                                                          boolean clearBeforeSave) {
        if (CloudUtils.isEmpty(productResult)) {
            return;
        }
        try {
            if (clearBeforeSave) {
                SQLite.delete(clazz).execute();
            }
        } catch (Exception e) {
        }
        processOnAsync(productResult, PROCESS_SAVE, null);
    }

    static public <T extends BaseObject> void saveToLocalFastly(final ProductResult<T> productResult, final Class<T> clazz,
                                                                boolean clearBeforeSave) {
        if (CloudUtils.isEmpty(productResult)) {
            return;
        }
        try {
            if (clearBeforeSave) {
                SQLite.delete(clazz).execute();
            }
        } catch (Exception e) {
        }
        FastStoreModelTransaction.saveBuilder(FlowManager.getModelAdapter(clazz)).addAll(productResult.list).build();
    }

    static public <T extends BaseModel> void clearTable(final Class<T> clazz) {
        SQLite.delete(clazz).execute();
    }

    static public <T extends BaseModel> void clearAllTable() {
        clearTable(Product.class);
        clearTable(Dictionary.class);
        clearTable(ProductContainer.class);
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
