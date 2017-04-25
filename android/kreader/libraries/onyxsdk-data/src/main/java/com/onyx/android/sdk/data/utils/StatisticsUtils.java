package com.onyx.android.sdk.data.utils;

import android.content.Context;

import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
import com.onyx.android.sdk.utils.MapUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public static void deleteStatisticsListByNote(final Context context,
                                                    final String note) {
        List<OnyxStatisticsModel> list = (List<OnyxStatisticsModel>) loadStatisticsListByNote(context, note);
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

    public static Collection<OnyxStatisticsModel> loadStatisticsListByNote(final Context context,
                                                                             final String note) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.note.eq(note));
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
                                                                     final String md5short,
                                                                     final int type,
                                                                     final Date fromTime) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.eq(type))
                .and(OnyxStatisticsModel_Table.md5short.eq(md5short))
                .and(OnyxStatisticsModel_Table.eventTime.greaterThan(fromTime));
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
                                                                                final boolean ascending) {
        Select select = new Select();
        Where where = select.from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.md5short.eq(md5short)).orderBy(OnyxStatisticsModel_Table.eventTime, ascending);
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

    public static List<Integer> getEventHourlyAgg(final List<OnyxStatisticsModel> statisticsList, final int count) {
        List<Integer> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(0);
        }
        for (OnyxStatisticsModel statisticsModel : statisticsList) {
            int hour = statisticsModel.getEventTime().getHours();
            if (hour < result.size()) {
                int value = result.get(hour);
                value ++;
                result.set(hour, value);
            }
        }
        return result;
    }

    public static Book getLongestBook(final List<OnyxStatisticsModel> statisticsModels) {
        Map<String, Long> timeMap = new HashMap<>();
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            String md5short = statisticsModel.getMd5short();
            long times = statisticsModel.getDurationTime();
            if (timeMap.containsKey(md5short)) {
                times = timeMap.get(md5short);
                times += statisticsModel.getDurationTime();
            }
            timeMap.put(md5short, times);
        }
        if (timeMap.size() == 0) {
            return null;
        }

        LinkedHashMap<String, Long> sortedMap = (LinkedHashMap<String, Long>) MapUtils.sortByValue(timeMap);
        Book book = new Book();
        Map.Entry last = MapUtils.getLast(sortedMap);
        book.setMd5short((String) last.getKey());
        book.setReadingTime((Long) last.getValue());
        return book;
    }

    public static Book getMostCarefullyBook(final List<OnyxStatisticsModel> statisticsModels) {
        Map<String, Long> countMap = new HashMap<>();
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            String md5short = statisticsModel.getMd5short();
            long count = 1;
            if (countMap.containsKey(md5short)) {
                count = countMap.get(md5short);
                count++;
            }
            countMap.put(md5short, count);
        }
        if (countMap.size() == 0) {
            return null;
        }

        LinkedHashMap<String, Long> sortedMap = (LinkedHashMap<String, Long>) MapUtils.sortByValue(countMap);
        Book book = new Book();
        Map.Entry last = MapUtils.getLast(sortedMap);
        book.setMd5short((String) last.getKey());
        return book;
    }

    public static List<Book> getRecentBooks(final List<OnyxStatisticsModel> statisticsModels, int count) {
        List<Book> recentBooks = new ArrayList<>();
        Set<String> bookMd5shorts = new LinkedHashSet<>();
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            if (bookMd5shorts.size() >= count) {
                break;
            }
            bookMd5shorts.add(statisticsModel.getMd5short());
        }
        for (String bookMd5short : bookMd5shorts) {
            Book book = new Book();
            book.setMd5short(bookMd5short);
            recentBooks.add(book);
        }
        return recentBooks;
    }

}
