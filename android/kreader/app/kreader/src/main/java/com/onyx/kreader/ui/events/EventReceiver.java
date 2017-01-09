package com.onyx.kreader.ui.events;


import android.content.Context;

import com.onyx.android.sdk.statistics.StatisticsBase;
import com.onyx.android.sdk.statistics.StatisticsManager;
import com.onyx.kreader.ui.settings.SystemSettingsActivity;

import org.apache.commons.collections4.map.HashedMap;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class EventReceiver {

    private boolean enable = true;
    private StatisticsManager statisticsManager = new StatisticsManager();
    private long lastTimestamp = 0;

    public EventReceiver(final Context context) {
        Map<String, String> args = new HashedMap<>();
        args.put(StatisticsBase.KEY_TAG, "5871bb2907fe65168c000f07");
        args.put(StatisticsBase.CHANNEL_TAG, "normal");
        statisticsManager.init(context, args);
    }

    public boolean isEnable() {
        return enable;
    }

    private void updateLastTimestamp() {
        lastTimestamp = System.currentTimeMillis();
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onDocumentOpenedEvent(event.getContext(), event.getPath(), event.getMd5());
        updateLastTimestamp();
    }

    @Subscribe
    public void onPageChanged(final PageChangedEvent event) {
        if (!isEnable()) {
            return;
        }
        event.setDuration((int)(System.currentTimeMillis() - lastTimestamp));
        updateLastTimestamp();
        statisticsManager.onPageChangedEvent(event.getContext(), event.getLastPage(), event.getCurrentPage(), event.getDuration());
    }

    @Subscribe
    public void onTextSelected(final TextSelectionEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onTextSelectedEvent(event.getContext(), event.getText());
    }

    @Subscribe
    public void onAddAnnotation(final AnnotationEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onAddAnnotationEvent(event.getContext(), event.getOriginText(), event.getUserNote());
    }

    @Subscribe
    public void onDictionaryLookup(final DictionaryLookupEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onDictionaryLookupEvent(event.getContext(), event.getText());
    }

}
