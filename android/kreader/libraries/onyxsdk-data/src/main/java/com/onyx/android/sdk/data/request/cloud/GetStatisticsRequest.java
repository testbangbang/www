package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.EventTypeAggBean;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Response;

/**
 * Created by ming on 2017/2/14.
 */

public class GetStatisticsRequest extends BaseCloudRequest {

    public final static int RECENT_BOOK_MAX_COUNT = 5;

    private Context context;
    private StatisticsResult statisticsResult;

    public GetStatisticsRequest(Context context) {
        this.context = context;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        statisticsResult = new StatisticsResult();
        if (DeviceUtils.isWifiConnected(context)) {
            readCloudData(parent);
        }else {
            readLocalData();
        }
    }

    private void readCloudData(CloudManager parent) throws Exception {
        Response<StatisticsResult> response = executeCall(ServiceFactory.getStatisticsService(parent.getCloudConf().getStatistics()).getStatistics(DeviceUtils.getMacAddress(context)));
        if (response != null && response.isSuccessful()) {
            statisticsResult = response.body();
            statisticsResult.setMyEventHourlyAgg(getSelfReadTimeDis());
        }
    }

    private void readLocalData() {
        EventTypeAggBean eventTypeAggBean = statisticsResult.getEventTypeAgg();
        statisticsResult.setTotalReadTime(getTotalReadTime());
        eventTypeAggBean.setRead(getReadCount());
        eventTypeAggBean.setFinish(getFinishCount());
        eventTypeAggBean.setAnnotation(getAnnotaionCount());
        statisticsResult.setMyEventHourlyAgg(getSelfReadTimeDis());
        statisticsResult.setDailyAvgReadTime(getReadTimeEveryDay());
        statisticsResult.setLongestReadTimeBook(getLongestBook());
        statisticsResult.setMostCarefulBook(getMostCarefullyBook());
        statisticsResult.setRecentReadingBooks(getRecentBooks());
    }

    private long getTotalReadTime() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        long readTimes = 0;
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            readTimes += statisticsModel.getDurationTime();
        }
        return readTimes;
    }

    private int getReadCount() {
        Set<String> readCount = new HashSet<>();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_OPEN);
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            readCount.add(statisticsModel.getMd5short());
        }
        return readCount.size();
    }

    private int getFinishCount() {
        Set<String> finishCount = new HashSet<>();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_FINISH);
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            finishCount.add(statisticsModel.getMd5short());
        }
        return finishCount.size();
    }

    private int getLookupDicCount() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        return statisticsModels.size();
    }

    private int getAnnotaionCount() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        return statisticsModels.size();
    }

    private List<Integer> getSelfReadTimeDis() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, date);
        return StatisticsUtils.getEventHourlyAgg(statisticsModels, 24);
    }

    private long getReadTimeEveryDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        Set<Integer> days = new HashSet<>();
        long readTimes = 0;
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, date, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            days.add(statisticsModel.getEventTime().getDay());
            readTimes += statisticsModel.getDurationTime();
        }
        if (days.size() == 0) {
            return 0;
        }
        return readTimes / days.size();
    }

    private Book getLongestBook() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        Book book = StatisticsUtils.getLongestBook(statisticsModels);
        if (book == null) {
            return null;
        }
        String md5short = book.getMd5short();

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_OPEN, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setBegin(statisticsModels.get(0).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_CLOSE, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setEnd(statisticsModels.get(statisticsModels.size() - 1).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    private Book getMostCarefullyBook() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION));
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED));
        Book book = StatisticsUtils.getMostCarefullyBook(statisticsModels);
        if (book == null) {
            return null;
        }
        String md5short = book.getMd5short();

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        book.setLookupDic(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        book.setAnnotation(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        book.setTextSelect(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    public List<Book> getRecentBooks() {
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, BaseStatisticsModel.DATA_TYPE_OPEN, false);
        List<Book> recentBooks = StatisticsUtils.getRecentBooks(statisticsModels, RECENT_BOOK_MAX_COUNT);

        for (Book book : recentBooks) {
            String md5short = book.getMd5short();
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5short);
            if (statisticsModels.size() > 0) {
                book.setName(statisticsModels.get(0).getName());
            }

            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_OPEN, true);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                book.setBegin(statisticsModels.get(0).getEventTime());
            }
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_CLOSE, true);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                book.setEnd(statisticsModels.get(statisticsModels.size() - 1).getEventTime());
            }

            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
            long useTime = 0;
            for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                useTime += statisticsModel.getDurationTime();
            }
            book.setReadingTime(useTime);
        }

        return recentBooks;
    }

    public StatisticsResult getStatisticsResult() {
        return statisticsResult;
    }
}
