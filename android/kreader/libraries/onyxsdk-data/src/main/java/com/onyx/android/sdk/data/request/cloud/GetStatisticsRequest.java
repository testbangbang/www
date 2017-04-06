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
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

/**
 * Created by ming on 2017/2/14.
 */

public class GetStatisticsRequest extends BaseCloudRequest {

    public final static int RECENT_BOOK_MAX_COUNT = 5;

    private Context context;
    private StatisticsResult statisticsResult;
    private String url;

    public GetStatisticsRequest(final Context context, final String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        statisticsResult = new StatisticsResult();
        readLocalData();
    }

    private void readCloudData(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(url)) {
            return;
        }
        String mac = DeviceUtils.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(mac)) {
            return;
        }
        try {
            Response<StatisticsResult> response = executeCall(ServiceFactory.getStatisticsService(url).getStatistics(mac));
            if (response != null && response.isSuccessful()) {
                statisticsResult = response.body();
                statisticsResult.setMyEventHourlyAgg(getSelfReadTimeDis());
            }else {
                readLocalData();
            }
        }catch (Exception e) {
            e.printStackTrace();
            readLocalData();
        }

    }

    private void readLocalData() {
        EventTypeAggBean eventTypeAggBean = statisticsResult.getEventTypeAgg();
        statisticsResult.setTotalReadTime(getTotalReadTime());
        eventTypeAggBean.setRead(getReadCount());
        eventTypeAggBean.setFinish(getFinishCount());
        eventTypeAggBean.setAnnotation(getAnnotationCount());
        eventTypeAggBean.setTextSelect(getSelectTextCount());
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

    private int getAnnotationCount() {
        List<OnyxStatisticsModel> annotationStatistics = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        return annotationStatistics.size();
    }

    private int getSelectTextCount() {
        List<OnyxStatisticsModel> highLightStatistics = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        return highLightStatistics.size();
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
        statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, true);
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

            Date beginTime = null;
            Date endTime = null;
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, BaseStatisticsModel.DATA_TYPE_OPEN, false);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                beginTime = statisticsModels.get(0).getEventTime();
                book.setBegin(beginTime);
            }
            statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListOrderByTime(context, md5short, false);
            if (statisticsModels != null && statisticsModels.size() > 0) {
                endTime = statisticsModels.get(0).getEventTime();
                book.setEnd(endTime);
            }

            long useTime = 0;
            if (beginTime != null) {
                statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, md5short, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE, beginTime);
                for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                    useTime += statisticsModel.getDurationTime();
                }
            }

            if (useTime <= 0 && beginTime != null & endTime != null) {
                useTime = endTime.getTime() - beginTime.getTime();
            }

            book.setReadingTime(Math.max(useTime, 0));
        }

        return recentBooks;
    }

    public StatisticsResult getStatisticsResult() {
        return statisticsResult;
    }
}
