package com.onyx.kreader.scribble.data;

import android.content.Context;
import com.onyx.kreader.utils.StringUtils;
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

    public static List<Scribble> loadScribblePage(final Context context,
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

    public static void saveScribblePage(final Context context,
                                        final String md5,
                                        final String pageName,
                                        final String subPageName,
                                        final List<Scribble> page) {

    }


    public static boolean remove(final Context context,
                                 final String md5,
                                 final String pageName,
                                 final String subPage) {
        return false;
    }

}
