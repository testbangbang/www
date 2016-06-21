package com.onyx.android.sdk.scribble.data;

import android.content.Context;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

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
        for(ShapeModel shapeModel : list) {
            shapeModel.save();
        }
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
