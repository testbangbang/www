package com.onyx.android.sdk.statistics;

import android.content.Context;

import java.util.Map;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class StatisticsManager {

    private StatisticsBase impl = new UMeng();


    public boolean init(final Context context, final Map<String, String> args) {
        return getImpl().init(context, args);
    }

    public void onActivityResume(final Context context) {
         getImpl().onActivityResume(context);
    }

    public void onActivityPause(final Context context) {
        getImpl().onActivityPause(context);
    }

    public void onDocumentOpenedEvent(final Context context, final String path, final String md5) {
        getImpl().onDocumentOpenedEvent(context, path, md5);
    }

    public void onPageChangedEvent(final Context context, final String last, final String current, int duration) {
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

    private StatisticsBase getImpl() {
        return impl;
    }

}
