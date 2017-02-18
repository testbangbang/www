package com.onyx.android.sdk.data.utils;

import android.content.Context;

import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ming on 2017/2/7.
 */

public class StatisticsUtils {

    public static void saveStatistics(final Context context, OnyxStatisticsModel statisticsData) {
        if (statisticsData == null) {
            return;
        }
        statisticsData.save();
    }

    public static void saveStatisticsList(final Context context,
                                          final Collection<OnyxStatisticsModel> list) {
        final DatabaseWrapper database = FlowManager.getDatabase(OnyxStatisticsDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(OnyxStatisticsModel statisticsData : list) {
            statisticsData.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void deleteStatisticsList(final Context context,
                                          final Collection<OnyxStatisticsModel> list) {
        final DatabaseWrapper database = FlowManager.getDatabase(OnyxStatisticsDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(OnyxStatisticsModel statisticsData : list) {
            statisticsData.delete();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static void deleteStatisticsListByStatus(final Context context,
                                            final int status) {
        List<OnyxStatisticsModel> list = (List<OnyxStatisticsModel>) loadStatisticsListByStatus(context, status);
        final DatabaseWrapper database = FlowManager.getDatabase(OnyxStatisticsDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(OnyxStatisticsModel statisticsData : list) {
            statisticsData.delete();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListByStatus(final Context context,
                                                                             final int count,
                                                                             final int status) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.status.eq(status)).limit(count);
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListByStatus(final Context context,
                                                                             final int status) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.status.eq(status));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final String md5short,
                                                                     final int type) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type)).and(OnyxStatisticsModel_Table.md5short.eq(md5short));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final int type) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final int type,
                                                                     final int status) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type)).and(OnyxStatisticsModel_Table.status.eq(status));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final Date fromTime) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.eventTime.greaterThan(fromTime));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final Date fromTime,
                                                                     final int type) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.eventTime.greaterThan(fromTime)).and(OnyxStatisticsModel_Table.type.eq(type));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListByMd5short(final Context context,
                                                                     final String md5short) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.md5short.eq(md5short));
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListOrderByTime(final Context context,
                                                                                final int type,
                                                                                final boolean ascending) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type)).orderBy(OnyxStatisticsModel_Table.eventTime, ascending);
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListOrderByTime(final Context context,
                                                                                final int type,
                                                                                final int status,
                                                                                final boolean ascending) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type)).and(OnyxStatisticsModel_Table.status.eq(status)).orderBy(OnyxStatisticsModel_Table.eventTime, ascending);
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsListOrderByTime(final Context context,
                                                                                final String md5short,
                                                                                final int type,
                                                                                final boolean ascending) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type)).and(OnyxStatisticsModel_Table.md5short.eq(md5short)).orderBy(OnyxStatisticsModel_Table.eventTime, ascending);
        List<OnyxStatisticsModel> list = where.queryList();
        return list;
    }

    public static String getBookMd5(final Context context,
                                    final String md5short) {
        String md5 = "";
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.md5short.eq(md5short)).limit(1);
        List<OnyxStatisticsModel> list = where.queryList();
        if (list != null && list.size() > 0) {
            md5 = list.get(0).getMd5();
        }
        return md5;
    }
}
