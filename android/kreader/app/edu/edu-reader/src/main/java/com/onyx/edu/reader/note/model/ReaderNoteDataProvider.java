package com.onyx.edu.reader.note.model;

import android.content.Context;
import com.onyx.android.sdk.scribble.data.ShapeDatabase;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhuzeng on 9/16/16.
 */
public class ReaderNoteDataProvider {


    public static abstract class DataProviderCallback {
        public abstract void finished();
    }

    public static void clear(final Context context) {
        new Delete().from(ReaderNoteDocumentModel.class).execute();
        new Delete().from(ReaderNoteShapeModel.class).execute();
    }

    public static ReaderNoteDocumentModel loadDocument(final Context context, final String uniqueId) {
        Select select = new Select();
        Where where = select.from(ReaderNoteDocumentModel.class).where(ReaderNoteDocumentModel_Table.uniqueId.eq(uniqueId));
        return (ReaderNoteDocumentModel) where.querySingle();
    }

    public static void saveDocument(final Context context, final ReaderNoteDocumentModel model) {
        if (model == null) {
            return;
        }
        model.save();
    }

    public static List<ReaderNoteShapeModel> loadShapeList(final Context context,
                                                 final String documentUniqueId,
                                                 final String pageUniqueId,
                                                 final String subPageUniqueId) {
        Select select = new Select();
        Where where = select.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.documentUniqueId.eq(documentUniqueId)).and(ReaderNoteShapeModel_Table.pageUniqueId.eq(pageUniqueId));
        if (StringUtils.isNotBlank(subPageUniqueId)) {
            where = where.and(ReaderNoteShapeModel_Table.subPageUniqueId.eq(subPageUniqueId));
        }

        List<ReaderNoteShapeModel> list = where.queryList();
        return list;
    }

    public static ReaderFormShapeModel loadFormShape(final Context context,
                                                           final String documentUniqueId,
                                                           final String formId) {
        Select select = new Select();
        Where where = select.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.documentUniqueId.eq(documentUniqueId)).and(ReaderFormShapeModel_Table.formId.eq(formId));
        return (ReaderFormShapeModel) where.querySingle();
    }

    public static List<ReaderFormShapeModel> loadFormShapeList(final Context context,
                                                           final String documentUniqueId,
                                                           final String pageUniqueId,
                                                           final String subPageUniqueId) {
        Select select = new Select();
        Where where = select.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.documentUniqueId.eq(documentUniqueId)).and(ReaderFormShapeModel_Table.pageUniqueId.eq(pageUniqueId));
        if (StringUtils.isNotBlank(subPageUniqueId)) {
            where = where.and(ReaderFormShapeModel_Table.subPageUniqueId.eq(subPageUniqueId));
        }

        List<ReaderFormShapeModel> list = where.queryList();
        return list;
    }

    public static List<ReaderFormShapeModel> loadFormShapeList(final Context context,
                                                               final String documentUniqueId,
                                                               final boolean review
                                                               ) {
        Select select = new Select();
        Where where = select.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.documentUniqueId.eq(documentUniqueId)).and(ReaderFormShapeModel_Table.review.eq(review));
        List<ReaderFormShapeModel> list = where.queryList();
        return list;
    }

    public static boolean hasFormShape(final Context context,
                                       final String shapeUniqueId) {
        Select select = new Select();
        Where where = select.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.shapeUniqueId.eq(shapeUniqueId));
        return where.queryList().size() > 0;
    }

    public static boolean hasUnLockFormShapes(final Context context,
                                              final String documentUniqueId,
                                              final boolean review) {
        Select select = new Select();
        Where where = select.from(ReaderFormShapeModel.class).
                where(ReaderFormShapeModel_Table.documentUniqueId.eq(documentUniqueId)).
                and(ReaderFormShapeModel_Table.lock.eq(false)).
                and(ReaderFormShapeModel_Table.review.eq(review));
        return where.queryList().size() > 0;
    }

    public static List<ReaderNoteShapeModel> loadShapeList(final Context context,
                                                           final String documentUniqueId) {
        Select select = new Select();
        Where where = select.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.documentUniqueId.eq(documentUniqueId));
        List<ReaderNoteShapeModel> list = where.queryList();
        return list;
    }

    public static void saveShapeList(final Context context,
                                     final Collection<ReaderNoteShapeModel> list) {
        final DatabaseWrapper database= FlowManager.getDatabase(ReaderNoteDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(ReaderNoteShapeModel shapeModel : list) {
            shapeModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void saveFormShapeList(final Context context,
                                     final Collection<ReaderFormShapeModel> list) {
        final DatabaseWrapper database= FlowManager.getDatabase(ReaderNoteDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(ReaderFormShapeModel shapeModel : list) {
            shapeModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static boolean removeFormShape(final Context context, String documentId, String formId) {
        Delete delete = new Delete();
        delete.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.documentUniqueId.eq(documentId)).and(ReaderFormShapeModel_Table.formId.eq(formId)).query();
        return true;
    }

    public static void saveDocumentList(final Context context,
                                     final Collection<ReaderNoteDocumentModel> list) {
        final DatabaseWrapper database= FlowManager.getDatabase(ReaderNoteDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(ReaderNoteDocumentModel documentModel : list) {
            documentModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void svaeShapeListInBackground(final Context context,
                                                 final Collection<ReaderNoteShapeModel> list,
                                                 final DataProviderCallback callback) {
        final DatabaseDefinition database= FlowManager.getDatabase(ReaderNoteDatabase.NAME);
        ProcessModelTransaction<ReaderNoteShapeModel> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<ReaderNoteShapeModel>() {
                    @Override
                    public void processModel(ReaderNoteShapeModel model) {
                        model.save();
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<ReaderNoteShapeModel>() {
                    @Override
                    public void onModelProcessed(long current, long total, ReaderNoteShapeModel modifiedModel) {
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
        delete.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.documentUniqueId.eq(documentUniqueId)).query();
    }

    public static boolean removeShape(final Context context,
                                      final String uniqueId) {
        Delete delete = new Delete();
        delete.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.shapeUniqueId.eq(uniqueId)).query();
        return true;
    }

    public static boolean removeShapesByIdList(final Context context, final List<String> list) {
        Delete delete = new Delete();
        delete.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.shapeUniqueId.in(list)).query();
        return true;
    }

    public static boolean removeFormShapesByIdList(final Context context, final List<String> list) {
        Delete delete = new Delete();
        delete.from(ReaderFormShapeModel.class).where(ReaderFormShapeModel_Table.shapeUniqueId.in(list)).query();
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
                delete.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.shapeUniqueId.in(list)).query();
                if (callback != null) {
                    callback.finished();
                }
            }
        }).build();
        transaction.execute();
    }

    public static boolean removePage(final Context context, final String pageName, final String subPageUniqueId) {
        Delete delete = new Delete();
        Where where = delete.from(ReaderNoteShapeModel.class).where(ReaderNoteShapeModel_Table.pageUniqueId.eq(pageName));
        if (StringUtils.isNotBlank(subPageUniqueId)) {
            where = where.and(ReaderNoteShapeModel_Table.subPageUniqueId.eq(subPageUniqueId));
        }
        where.query();
        return true;
    }


}
