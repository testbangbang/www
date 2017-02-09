package com.onyx.android.sdk.statistics;

import android.content.Context;
import android.net.ConnectivityManager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.cloud.PushStatisticsRequest;
import com.onyx.android.sdk.data.request.data.GetFileMd5Request;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ming on 2017/2/7.
 */

public class OnyxStatistics implements StatisticsBase {

    private final static int PUSH_THRESHOLD_VALUE = 5;

    private CloudStore cloudStore;
    private String sessionId;
    private String md5;
    private String md5short;
    private String mac;

    private int newAddCounter = 0;
    private boolean flushing = false;

    @Override
    public boolean init(Context context, Map<String, String> args) {
        cloudStore = new CloudStore();
        setFlushing(false);
        return false;
    }

    private OnyxStatisticsModel getStatisticsData(Context context, int type) {
        if (StringUtils.isNullOrEmpty(mac)) {
            mac = DeviceUtils.getMacAddress(context);
        }
        OnyxStatisticsModel statisticsData = new OnyxStatisticsModel();
        statisticsData.setMd5(md5);
        statisticsData.setMd5short(md5short);
        statisticsData.setMac(mac);
        statisticsData.setSid(sessionId);
        statisticsData.setType(type);
        statisticsData.setEventTime(new Date());
        return statisticsData;
    }

    private void saveToCloud(final Context context, final OnyxStatisticsModel statisticsData) {
        StatisticsUtils.saveStatistics(context, statisticsData);
        increaseCounter();
        if (getNewAddCounter() >= PUSH_THRESHOLD_VALUE) {
            flushStatistics(context);
        }
    }

    private void flushStatistics(final Context context) {
        if (!DeviceUtils.isWifiConnected(context) || isFlushing()) {
            return;
        }
        setFlushing(true);
        PushStatisticsRequest statisticsRequest = new PushStatisticsRequest(context);
        cloudStore.submitRequest(context, statisticsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setFlushing(false);
                resetCounter();
            }
        });
    }

    @Override
    public void onActivityResume(Context context) {
        flushStatistics(context);
    }

    @Override
    public void onActivityPause(Context context) {

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
        final OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_OPEN);
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
                    StatisticsUtils.saveStatistics(context, statisticsData);
                    flushStatistics(context);
                }
            });
        }else {
            statisticsData.setMd5(md5);
            this.md5 = md5;
            StatisticsUtils.saveStatistics(context, statisticsData);
            flushStatistics(context);
        }
    }

    @Override
    public void onDocumentClosed(Context context) {
        OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_CLOSE);
        StatisticsUtils.saveStatistics(context, statisticsData);
        flushStatistics(context);
    }

    @Override
    public void onPageChangedEvent(Context context, String last, String current, long duration) {
        OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE);
        statisticsData.setLastPage(Integer.valueOf(last));
        statisticsData.setCurrPage(Integer.valueOf(current));
        statisticsData.setDurationTime(duration);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onTextSelectedEvent(Context context, String text) {
        OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_TEXT_SELECTED);
        statisticsData.setOrgText(text);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onAddAnnotationEvent(Context context, String originText, String userNote) {
        OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_ANNOTATION);
        statisticsData.setOrgText(originText);
        statisticsData.setNote(userNote);
        saveToCloud(context, statisticsData);
    }

    @Override
    public void onDictionaryLookupEvent(Context context, String originText) {
        OnyxStatisticsModel statisticsData = getStatisticsData(context, BaseStatisticsModel.DATA_TYPE_LOOKUP_DIC);
        statisticsData.setOrgText(originText);
        saveToCloud(context, statisticsData);
    }

    public int getNewAddCounter() {
        return newAddCounter;
    }

    private void resetCounter() {
        newAddCounter = 0;
    }

    private void increaseCounter() {
        newAddCounter++;
    }

    public boolean isFlushing() {
        return flushing;
    }

    public void setFlushing(boolean flushing) {
        this.flushing = flushing;
    }
}
