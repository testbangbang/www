package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.transaction.ProcessDeleteModel;
import com.onyx.android.sdk.data.transaction.ProcessSaveModel;
import com.onyx.android.sdk.data.transaction.ProcessUpdateModel;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
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

    static public <T extends BaseData> void processOnAsync(final List<T> productResult, final int process,
                                                             ProcessModelTransaction.OnModelProcessListener<T> processListener) {
        ProcessModelTransaction<T> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<T>() {
                    @Override
                    public void processModel(T model) {
                        if (process == PROCESS_SAVE) {
                            model.save();
                        } else if (process == PROCESS_DELETE) {
                            model.delete();
                        } else if (process == PROCESS_UPDATE) {
                            model.update();
                        }
                    }
                }).processListener(processListener).addAll(productResult).build();
        Transaction transaction = FlowManager.getDatabase(OnyxCloudDatabase.class)
                .beginTransactionAsync(processModelTransaction).build();
        transaction.execute();
    }

    static public <T extends BaseData> void saveToLocal(Class<?> databaseClass, final List<T> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        final DatabaseWrapper database = FlowManager.getDatabase(databaseClass).getWritableDatabase();
        database.beginTransaction();
        for (T t : list) {
            t.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    static public <T extends BaseData> void saveToLocal(final ProductResult<T> productResult, final Class<T> clazz,
                                                          boolean clearBeforeSave) {
        if (clearTable(productResult, clazz, clearBeforeSave)) {
            processOnAsync(productResult.list, PROCESS_SAVE, null);
        }
    }

    static public <T extends BaseData> void saveToLocal(final List<T> list, final Class<T> clazz,
                                                          boolean clearBeforeSave) {
        if (clearBeforeSave) {
            clearTable(clazz);
        }
        processOnAsync(list, PROCESS_SAVE, null);
    }

    static public <T extends BaseData> void saveToLocalFast(final ProductResult<T> productResult, final Class<T> clazz,
                                                            boolean clearBeforeSave) {
        if (clearTable(productResult, clazz, clearBeforeSave)) {
            saveToLocalFast(productResult.list, clazz);
        }
    }

    static public <T extends BaseData> void saveToLocalFast(final List<T> productResult, final Class<T> clazz,
                                                            boolean clearBeforeSave) {
        if (clearBeforeSave) {
            if (productResult.size() <= 0) {
                return;
            }
            clearTable(clazz);
        }
        saveToLocalFast(productResult, clazz);
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

    static public <T extends BaseModel> List<T> queryDataList(final Class<T> clazz, OrderBy... orderBys) {
        Where<T> where = SQLite.select().from(clazz).where();
        for (OrderBy orderBy : orderBys) {
            where.orderBy(orderBy);
        }
        return where.queryList();
    }

    static public <T extends BaseModel> T queryDataSingle(final Class<T> clazz, SQLCondition... conditions) {
        return SQLite.select().from(clazz).where().andAll(conditions).querySingle();
    }

    static public <T extends BaseModel> T queryDataSingle(final Class<T> clazz, OrderBy... orderBys) {
        Where<T> where = SQLite.select().from(clazz).where();
        for (OrderBy orderBy : orderBys) {
            where.orderBy(orderBy);
        }
        return where.querySingle();
    }

    static public <T extends BaseModel> long queryDataCount(final Class<T> clazz) {
        return new Select(Method.count()).from(clazz).count();
    }

    static public <T extends BaseModel> long queryDataCount(final Class<T> clazz, Condition... condition) {
        Where where = new Select(Method.count()).from(clazz).where();
        if (condition != null && condition.length > 0) {
            where.andAll(condition);
        }
        return where.count();
    }

    static public <T extends BaseData> void saveAsyncSingle(final BaseData object){
        object.async().save();
    }

    static public <T extends BaseData> boolean isEmpty(final ProductResult<T> productResult) {
        if (productResult == null || productResult.list == null || productResult.list.size() <= 0) {
            return true;
        }
        return false;
    }

    static public <T extends BaseData> List<T> getResultList(final ProductResult<T> productResult) {
        if (isEmpty(productResult)) {
            return new ArrayList<>();
        }
        return productResult.list;
    }
}
