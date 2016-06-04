package com.onyx.kreader.scribble.data;

import android.content.Context;
import com.onyx.kreader.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.*;

/**
 * Created by zhuzeng on 9/16/15.
 * CRUD for shape data.
 */
public class ShapeDataProvider {

    private static final String TAG = ShapeDataProvider.class.getSimpleName();

    public static List<ShapeModel> loadShapeList(final Context context,
                                                 final String md5,
                                                 final String pageName,
                                                 final String subPageName) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.md5.eq(md5)).and(ShapeModel_Table.pageName.eq(pageName));
        if (StringUtils.isNotBlank(subPageName)) {
            where = where.and(ShapeModel_Table.subPageName.eq(subPageName));
        }

        List<ShapeModel> list = where.queryList();
        return list;
    }

    public static void saveShapeList(final Context context,
                                     final List<ShapeModel> list) {
        for(ShapeModel shapeModel : list) {
            shapeModel.save();
        }
    }

    public static boolean removeShape(final Context context,
                                      final String uniqueId) {
        Select select = new Select();
        Where where = select.from(ShapeModel.class).where(ShapeModel_Table.uniqueId.eq(uniqueId));
        where.querySingle().delete();
        return true;
    }


}
