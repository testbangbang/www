package com.onyx.android.sdk.data.utils;

import android.content.Context;

import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.Collection;
import java.util.List;

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
        final DatabaseWrapper database= FlowManager.getDatabase(OnyxStatisticsDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for(OnyxStatisticsModel statisticsData : list) {
            statisticsData.save();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public static Collection<OnyxStatisticsModel> loadStatisticsList(final Context context,
                                                                     final int count,
                                                                     final int state) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.status.eq(BaseStatisticsModel.DATA_STATUS_NOT_PUSH)).limit(count);
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
