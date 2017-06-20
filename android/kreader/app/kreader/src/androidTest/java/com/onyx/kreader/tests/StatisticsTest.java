package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ming on 2017/2/17.
 */

public class StatisticsTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public StatisticsTest() {
        super(ReaderTestActivity.class);
    }

    public void testEventHourlyAgg() {
        StatisticsUtils.deleteStatisticsListByStatus(getActivity(), BaseStatisticsModel.DATA_STATUS_TEST);
        int maxHour = 24;
        final String md5 = UUID.randomUUID().toString();
        List<Integer> eventHourlyAgg = new ArrayList<>();
        for (int i = 0; i < maxHour; i++) {
            eventHourlyAgg.add(TestUtils.randInt(0, 100));
        }
        List<OnyxStatisticsModel> testStatistics = new ArrayList<>();
        for (int i = 0; i < maxHour; i++) {
            Date date = new Date();
            date.setHours(i);
            int eventCount = eventHourlyAgg.get(i);
            for (int j = 0; j < eventCount; j++) {
                OnyxStatisticsModel statistics = OnyxStatisticsModel.create(md5, null, null, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE, date, BaseStatisticsModel.DATA_STATUS_TEST);
                testStatistics.add(statistics);
            }
        }
        StatisticsUtils.saveStatisticsList(getActivity(), testStatistics);

        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByStatus(getActivity(), BaseStatisticsModel.DATA_STATUS_TEST);
        assertTrue(statisticsModels.size() > 0);
        List<Integer> result = StatisticsUtils.getEventHourlyAgg(statisticsModels, maxHour);
        assertTrue(result.size() == maxHour);
        StatisticsUtils.deleteStatisticsList(getActivity(), statisticsModels);

        for (int i = 0; i < result.size(); i++) {
            int eventCount = result.get(i);
            assertTrue(eventCount == eventHourlyAgg.get(i));
        }
    }

    public void testLongestBook() {
        StatisticsUtils.deleteStatisticsListByStatus(getActivity(), BaseStatisticsModel.DATA_STATUS_TEST);
        Date date = new Date();
        String randomMd5short = "";
        List<OnyxStatisticsModel> testStatistics = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            randomMd5short = UUID.randomUUID().toString();
            OnyxStatisticsModel statistics = OnyxStatisticsModel.create(null, randomMd5short, null, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE, date);
            statistics.setStatus(BaseStatisticsModel.DATA_STATUS_TEST);
            statistics.setDurationTime(Long.valueOf(i));
            testStatistics.add(statistics);
        }
        StatisticsUtils.saveStatisticsList(getActivity(), testStatistics);

        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(getActivity(), BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE, BaseStatisticsModel.DATA_STATUS_TEST);
        Book longestBook = StatisticsUtils.getLongestBook(statisticsModels);
        StatisticsUtils.deleteStatisticsList(getActivity(), testStatistics);
        assertTrue(longestBook != null);
        assertTrue(randomMd5short.equals(longestBook.getMd5short()));
    }

    public void testCarefullyBook() {
        StatisticsUtils.deleteStatisticsListByStatus(getActivity(), BaseStatisticsModel.DATA_STATUS_TEST);
        String randomMd5short = "";
        List<OnyxStatisticsModel> testStatistics = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Date date = new Date();
            randomMd5short = UUID.randomUUID().toString();
            for (int j = 0; j < i + 1; j++) {
                testStatistics.add(OnyxStatisticsModel.create(null, randomMd5short, null, BaseStatisticsModel.DATA_TYPE_ANNOTATION, date, BaseStatisticsModel.DATA_STATUS_TEST));
                testStatistics.add(OnyxStatisticsModel.create(null, randomMd5short, null, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED, date, BaseStatisticsModel.DATA_STATUS_TEST));
                testStatistics.add(OnyxStatisticsModel.create(null, randomMd5short, null, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC, date, BaseStatisticsModel.DATA_STATUS_TEST));
            }
        }
        StatisticsUtils.saveStatisticsList(getActivity(), testStatistics);

        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(getActivity(), BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC, BaseStatisticsModel.DATA_STATUS_TEST);
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(getActivity(), BaseStatisticsModel.DATA_TYPE_ANNOTATION, BaseStatisticsModel.DATA_STATUS_TEST));
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(getActivity(), BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED, BaseStatisticsModel.DATA_STATUS_TEST));
        Book mostCarefullyBook = StatisticsUtils.getMostCarefullyBook(statisticsModels);
        StatisticsUtils.deleteStatisticsList(getActivity(), testStatistics);
        assertTrue(mostCarefullyBook != null);
        assertTrue(randomMd5short.equals(mostCarefullyBook.getMd5short()));
    }

    public void testRecentBooks() {
        StatisticsUtils.deleteStatisticsListByStatus(getActivity(), BaseStatisticsModel.DATA_STATUS_TEST);
        int testCount = 20;
        String md5short;
        List<OnyxStatisticsModel> testStatistics = new ArrayList<>();
        List<String> md5List = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            md5short = UUID.randomUUID().toString();
            Date date = new Date();
            date.setSeconds(i);
            OnyxStatisticsModel statistics = OnyxStatisticsModel.create(null, md5short, null, BaseStatisticsModel.DATA_TYPE_OPEN, date, BaseStatisticsModel.DATA_STATUS_TEST);
            testStatistics.add(statistics);
            md5List.add(md5short);
        }
        StatisticsUtils.saveStatisticsList(getActivity(), testStatistics);

        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(getActivity(), BaseStatisticsModel.DATA_TYPE_OPEN, BaseStatisticsModel.DATA_STATUS_TEST, false);
        StatisticsUtils.deleteStatisticsList(getActivity(), testStatistics);
        List<Book> recentBooks = StatisticsUtils.getRecentBooks(statisticsModels, testCount);
        assertTrue(md5List.size() == statisticsModels.size());
        assertTrue(recentBooks != null);
        assertTrue(recentBooks.size() == statisticsModels.size());
        int index = 0;
        for (Book book : recentBooks) {
            String bookMd5short = book.getMd5short();
            assertTrue(bookMd5short != null);
            assertTrue(bookMd5short.equals(md5List.get(md5List.size() - index - 1)));
            index++;
        }
    }
}
