package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.DefaultTransactionManager;

import java.util.*;

/**
 * Created by zhuzeng on 9/16/15.
 * CRUD for shape data.
 */
public class ShapeDataProvider {

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

    public static void saveShapeList(final Context context,
                                     final Collection<ShapeModel> list) {
        final DatabaseWrapper database= FlowManager.getDatabase(ShapeDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(ShapeModel shapeModel : list) {
            shapeModel.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void removeAllShapeOfDocument(final Context context, final String documentUniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.documentUniqueId.eq(documentUniqueId));
        where.querySingle().delete();
    }

    public static boolean removeShape(final Context context,
                                      final String uniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.shapeUniqueId.eq(uniqueId));
        where.querySingle().delete();
        return true;
    }

    public static boolean removePage(final Context context, final String pageUniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.pageUniqueId.eq(pageUniqueId));
        where.querySingle().delete();
        return true;
    }




}
