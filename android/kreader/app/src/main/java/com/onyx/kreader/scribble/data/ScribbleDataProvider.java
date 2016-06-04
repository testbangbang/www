package com.onyx.kreader.scribble.data;

import android.content.Context;
import com.onyx.kreader.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.io.File;
import java.util.*;

/**
 * Created by zhuzeng on 9/16/15.
 * CRUD for scribble page.
 */
public class ScribbleDataProvider {

    private static final String TAG = ScribbleDataProvider.class.getSimpleName();

    public static List<Scribble> loadScribbleList(final Context context,
                                                  final String md5,
                                                  final String pageName,
                                                  final String subPageName) {
        Select select = new Select();
        Where where = select.from(Scribble.class).where(Scribble_Table.md5.eq(md5)).and(Scribble_Table.pageName.eq(pageName));
        if (StringUtils.isNotBlank(subPageName)) {
            where = where.and(Scribble_Table.subPageName.eq(subPageName));
        }

        List<Scribble> list = where.queryList();
        return list;
    }

    public static void saveScribbleList(final Context context,
                                        final List<Scribble> list) {
        for(Scribble scribble : list) {
            scribble.save();
        }
    }

    public static boolean remove(final Context context,
                                 final String uniqueId) {
        Select select = new Select();
        Where where = select.from(Scribble.class).where(Scribble_Table.uniqueId.eq(uniqueId));
        where.querySingle().delete();
        return true;
    }


}
