package com.onyx.android.sdk.statistics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.StatisticsCloudManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.cloud.PushStatisticsRequest;
import com.onyx.android.sdk.data.request.data.fs.GetFileMd5Request;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ming on 2017/2/7.
 */

public class OnyxStatistics implements StatisticsBase {

    private final static int PUSH_THRESHOLD_VALUE = 5;

    private StatisticsCloudManager cloudManager;
    private String sessionId;
    private String md5;
    private String md5short;
    private String url;

    private List<OnyxStatisticsModel> statisticsQueue;

    @Override
    public boolean init(Context context, Map<String, String> args) {
        cloudManager = new StatisticsCloudManager();
        statisticsQueue = new ArrayList<>();
        url = args.get(STATISTICS_URL);
        return true;
    }

    private OnyxStatisticsModel createStatisticsData(Context context, int type) {
        return OnyxStatisticsModel.create(md5, md5short, sessionId, type, new Date());
    }

    private void saveToCloud(final Context context, final OnyxStatisticsModel statisticsModel) {
        statisticsQueue.add(statisticsModel);
        if (statisticsQueue.size() >= PUSH_THRESHOLD_VALUE) {
            flushStatistics(context);
        }
    }

    private void flushStatistics(final Context context) {
        PushStatisticsRequest statisticsRequest = new PushStatisticsRequest(context, statisticsQueue, url);
        statisticsQueue = new ArrayList<>();
        cloudManager.submitRequest(context, statisticsRequest, null);
    }

    @Override
    public void onActivityResume(Context context) {
        flushStatistics(context);
    }

    @Override
    public void onActivityPause(Context context) {
        flushStatistics(context);
    }

    @Override
    public void onNetworkChanged(Context context, boolean connected, int networkType) {
        if (connected && networkType == ConnectivityManager.TYPE_WIFI) {
            flushStatistics(context);
        }
    }

    @Override
    public void onDocumentOpenedEvent(final Context context, final DocumentInfo documentInfo) {
        if (context == null) {
            return;
        }
        sessionId = UUID.randomUUID().toString();
        this.md5short = documentInfo.getMd5();
        String md5 = StatisticsUtils.getBookMd5(context, md5short);
        final OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_OPEN);
        statisticsData.setPath(documentInfo.getPath());
        statisticsData.setTitle(documentInfo.getTitle());
        statisticsData.setName(documentInfo.getName());
        statisticsData.setAuthor(documentInfo.getAuthors());
        if (StringUtils.isNullOrEmpty(md5)) {
            DataManager dataManager = new DataManager();
            final GetFileMd5Request md5Request = new GetFileMd5Request(documentInfo.getPath());
            dataManager.submit(context, md5Request, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    statisticsData.setMd5(md5Request.getMd5());
                    OnyxStatistics.this.md5 = md5Request.getMd5();
                    statisticsQueue.add(statisticsData);
                    flushStatistics(context);
                }
            });
        }else {
            statisticsData.setMd5(md5);
            this.md5 = md5;
            statisticsQueue.add(statisticsData);
            flushStatistics(context);
        }
    }

    @Override
    public void onDocumentClosed(Context context) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_CLOSE);
        statisticsQueue.add(statisticsData);
        flushStatistics(context);
    }

    @Override
    public void onPageChangedEvent(Context context, String last, String current, long duration) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        statisticsData.setLastPage(StringUtils.isNullOrEmpty(last) ? 0 : Integer.valueOf(last));
        statisticsData.setCurrPage(StringUtils.isNullOrEmpty(current) ? 0 : Integer.valueOf(current));
        statisticsData.setDurationTime(duration);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onTextSelectedEvent(Context context, String text) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        statisticsData.setOrgText(text);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onAddAnnotationEvent(Context context, String originText, String userNote) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        statisticsData.setOrgText(originText);
        statisticsData.setNote(userNote);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onDictionaryLookupEvent(Context context, String originText) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        statisticsData.setOrgText(originText);
        saveToCloud(context, statisticsData);
    }

    public void onDocumentFinished(final Context context, final String comment, final int score) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_FINISH);
        statisticsData.setComment(comment);
        statisticsData.setScore(score);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onBatteryStatusChange(final Context context, final String status, final int level) {
        OnyxStatisticsModel statisticsData = createStatisticsData(context, BaseStatisticsModel.DATA_TYPE_BATTERY);
        statisticsData.setName(status);
        statisticsData.setScore(level);
        saveToCloud(context, statisticsData);
    }
}
