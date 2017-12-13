package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.*;

import java.util.*;

/**
 * Created by zhuzeng on 9/16/15.
 * CRUD for shape data.
 */
public class ShapeDataProvider {


    public static abstract class DataProviderCallback {
        public abstract void finished();
    }

    public static void clear() {
        new Delete().from(ShapeModel.class).execute();
    }

    public static List<ShapeModel> loadShapeList(final Context context,
                                                 final String documentUniqueId,
                                                 final String pageUniqueId,
                                                 final String subPageName) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.documentUniqueId.eq(documentUniqueId)).and(ShapeModel_Table.pageUniqueId.eq(pageUniqueId));
        if (StringUtils.isNotBlank(subPageName)) {
            where = where.and(ShapeModel_Table.subPageName.eq(subPageName));
        }

        List<ShapeModel> list = where.queryList();
        return list;
    }

    public static List<ShapeModel> loadSubPageShapes(final Context context,
                                                 final String documentUniqueId,
                                                 final String pageUniqueId,
                                                 final String subPageName) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.documentUniqueId.eq(documentUniqueId));
        if (StringUtils.isNotBlank(subPageName)) {
            where = where.and(ShapeModel_Table.subPageName.eq(subPageName));
        }

        List<ShapeModel> list = where.queryList();
        return list;
    }

    public static List<ShapeModel> loadShapeList(final Context context) {
        return new Select().from(ShapeModel.class).queryList();
    }

    public static void saveShapeList(final Context context,
                                     final Collection<ShapeModel> list) {
        final DatabaseWrapper database = FlowManager.getDatabase(ShapeDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (ShapeModel shapeModel : list) {
            shapeModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void updateShapeList(final Context context,
                                       final Collection<ShapeModel> list) {
        final DatabaseWrapper database = FlowManager.getDatabase(ShapeDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (ShapeModel shapeModel : list) {
            removeShape(context, shapeModel.shapeUniqueId);
            shapeModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void saveShape(final Context context,
                                 final ShapeModel shapeModel) {
        if (shapeModel == null) {
            return;
        }
        shapeModel.save();
    }

    public static void updateShape(final Context context,
                                   final ShapeModel shapeModel){
        if (shapeModel == null){
            return;
        }
        shapeModel.update();
    }

    public static void saveShapeListInBackground(final Context context,
                                                 final Collection<ShapeModel> list,
                                                 final DataProviderCallback callback) {
        final DatabaseDefinition database= FlowManager.getDatabase(ShapeDatabase.NAME);
        ProcessModelTransaction<ShapeModel> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<ShapeModel>() {
                    @Override
                    public void processModel(ShapeModel model) {
                        model.save();
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<ShapeModel>() {
                    @Override
                    public void onModelProcessed(long current, long total, ShapeModel modifiedModel) {
                        if (callback != null && current >= total - 1) {
                            callback.finished();
                        }
                    }
                }).addAll(list).build();
        Transaction transaction = database.beginTransactionAsync(processModelTransaction).build();
        transaction.execute();
    }

    public static void removeAllShapeOfDocument(final Context context, final String documentUniqueId) {
        Delete delete = new Delete();
        delete.from(ShapeModel.class).where(ShapeModel_Table.documentUniqueId.eq(documentUniqueId)).query();
    }

    public static boolean removeShape(final Context context,
                                      final String uniqueId) {
        Delete delete = new Delete();
        delete.from(ShapeModel.class).where(ShapeModel_Table.shapeUniqueId.eq(uniqueId)).query();
        return true;
    }

    public static boolean removeShapesByIdList(final Context context, final List<String> list) {
        Delete delete = new Delete();
        delete.from(ShapeModel.class).where(ShapeModel_Table.shapeUniqueId.in(list)).query();
        return true;
    }

    public static void removeShapesByIdListInBackground(final Context context,
                                                        final List<String> list,
                                                        final DataProviderCallback callback) {
        final DatabaseDefinition database= FlowManager.getDatabase(ShapeDatabase.NAME);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                Delete delete = new Delete();
                delete.from(ShapeModel.class).where(ShapeModel_Table.shapeUniqueId.in(list)).query();
                if (callback != null) {
                    callback.finished();
                }
            }
        }).build();
        transaction.execute();
    }

    public static boolean removePage(final Context context, final String pageUniqueId) {
        Delete delete = new Delete();
        delete.from(ShapeModel.class).where(ShapeModel_Table.pageUniqueId.eq(pageUniqueId)).query();
        return true;
    }




}
