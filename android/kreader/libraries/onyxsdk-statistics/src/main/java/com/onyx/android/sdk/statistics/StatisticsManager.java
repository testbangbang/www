package com.onyx.android.sdk.statistics;

import android.content.Context;
import android.os.BatteryManager;

import com.onyx.android.sdk.data.model.DocumentInfo;

import java.util.Map;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class StatisticsManager {

    private StatisticsBase impl;

    public enum StatisticsType {
        UMeng,
        Onyx,
    }

    public void setImpl(StatisticsBase impl) {
        this.impl = impl;
    }

    public boolean init(final Context context, final Map<String, Object> args, final StatisticsType type) {
        switch (type) {
            case UMeng:
                setImpl(new UMeng());
                break;
            case Onyx:
                setImpl(new OnyxStatistics());
                break;
        }
        return getImpl().init(context, args);
    }

    public void onActivityResume(final Context context) {
         getImpl().onActivityResume(context);
    }

    public void onActivityPause(final Context context) {
        getImpl().onActivityPause(context);
    }

    public void onDocumentOpenedEvent(final Context context, final DocumentInfo documentInfo) {
        getImpl().onDocumentOpenedEvent(context, documentInfo);
    }

    public void onDocumentClosed(final Context context) {
        getImpl().onDocumentClosed(context);
    }

    public void onPageChangedEvent(final Context context, final String last, final String current, long duration) {
        getImpl().onPageChangedEvent(context, last, current, duration);
    }

    public void onTextSelectedEvent(final Context context, final String text) {
        getImpl().onTextSelectedEvent(context, text);
    }

    public void onAddAnnotationEvent(final Context context, final String originText, final String userNote) {
        getImpl().onAddAnnotationEvent(context, originText, userNote);
    }

    public void onDictionaryLookupEvent(final Context context, final String originText) {
        getImpl().onDictionaryLookupEvent(context, originText);
    }

    public void onDocumentFinished(final Context context, final String comment, final int score) {
        getImpl().onDocumentFinished(context, comment, score);
    }

    public void onNetworkChangedEvent(final Context context, boolean connected, int networkType) {
        getImpl().onNetworkChanged(context, connected, networkType);
    }

    public void onBatteryStatusChange(final Context context, final int status, final int level) {
        String statusStr = "unknown";
        switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusStr = "unknown";
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusStr = "charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusStr = "discharging";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusStr = "not charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusStr = "full";
                break;
        }
        getImpl().onBatteryStatusChange(context, statusStr, level);
    }

    public void onFormFieldSelectedEvent(Context context, String formId, String value) {
        getImpl().onFormFieldSelected(context, formId, value);
    }

    private StatisticsBase getImpl() {
        return impl;
    }
}
