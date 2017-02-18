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
        List<Integer> selfReadTimeDis = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            selfReadTimeDis.add(0);
        }
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            int hour = statisticsModel.getEventTime().getHours();
            if (hour < selfReadTimeDis.size()) {
                int value = selfReadTimeDis.get(hour);
                value ++;
                selfReadTimeDis.set(hour, value);
            }

        }
        return selfReadTimeDis;
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
        Map<String, Long> timeMap = new HashMap<>();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
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
        Book book = new Book();

        Collection<Long> c = timeMap.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        long maxValue = (long) obj[obj.length-1];
        String md5shortOfMaxValue = "";
        for (String md5 : timeMap.keySet()) {
            if (maxValue == timeMap.get(md5)) {
                md5shortOfMaxValue = md5;
            }
        }

        book.setReadingTime(maxValue);
        book.setMd5short(md5shortOfMaxValue);

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5shortOfMaxValue, BaseStatisticsModel.DATA_TYPE_OPEN, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setBegin(statisticsModels.get(0).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5shortOfMaxValue, BaseStatisticsModel.DATA_TYPE_CLOSE, true);
        if (statisticsModels != null && statisticsModels.size() > 0) {
            book.setEnd(statisticsModels.get(statisticsModels.size() - 1).getEventTime());
        }
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5shortOfMaxValue);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    private Book getMostCarefullyBook() {
        Map<String, Long> countMap = new HashMap<>();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION));
        statisticsModels.addAll(StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED));
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
        Book book = new Book();

        Collection<Long> c = countMap.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        long maxValue = (long) obj[obj.length-1];
        String md5shortOfMaxValue = "";
        for (String md5 : countMap.keySet()) {
            if (maxValue == countMap.get(md5)) {
                md5shortOfMaxValue = md5;
            }
        }
        book.setMd5short(md5shortOfMaxValue);

        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5shortOfMaxValue, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        book.setLookupDic(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5shortOfMaxValue, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        book.setAnnotation(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5shortOfMaxValue, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        book.setTextSelect(statisticsModels.size());
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, md5shortOfMaxValue);
        if (statisticsModels.size() > 0) {
            book.setName(statisticsModels.get(0).getName());
        }
        return book;
    }

    public List<Book> getRecentBooks() {
        List<Book> recentBooks = new ArrayList<>();
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, BaseStatisticsModel.DATA_TYPE_OPEN, false);
        Set<String> bookMd5shorts = new LinkedHashSet<>();
        for (OnyxStatisticsModel statisticsModel : statisticsModels) {
            if (bookMd5shorts.size() >= RECENT_BOOK_MAX_COUNT) {
                break;
            }
            bookMd5shorts.add(statisticsModel.getMd5short());
        }

        for (String bookMd5short : bookMd5shorts) {
            Book book = new Book();
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByMd5short(context, bookMd5short);
            if (statisticsModels.size() > 0) {
                book.setName(statisticsModels.get(0).getName());
            }

            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, bookMd5short, BaseStatisticsModel.DATA_TYPE_OPEN, true);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                book.setBegin(statisticsModels.get(0).getEventTime());
            }
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, bookMd5short, BaseStatisticsModel.DATA_TYPE_CLOSE, true);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                book.setEnd(statisticsModels.get(statisticsModels.size() - 1).getEventTime());
            }

            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, bookMd5short, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
            long useTime = 0;
            for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                useTime += statisticsModel.getDurationTime();
            }
            book.setReadingTime(useTime);

            recentBooks.add(book);
        }

        return recentBooks;
    }

    public StatisticsResult getStatisticsResult() {
        return statisticsResult;
    }
}
