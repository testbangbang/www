package com.onyx.edu.reader.ui.events;


import android.content.Context;

import com.onyx.android.sdk.statistics.StatisticsBase;
import com.onyx.android.sdk.statistics.StatisticsManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.device.DeviceConfig;

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

    private StatisticsManager.StatisticsType statisticsType = StatisticsManager.StatisticsType.Onyx;

    public EventReceiver(final Context context) {
        Map<String, String> args = new HashedMap<>();
        switch (statisticsType) {
            case UMeng:
                final String key = DeviceConfig.sharedInstance(context).getUmengKey();
                final String channel = DeviceConfig.sharedInstance(context).getChannel();
                if (StringUtils.isBlank(key) || StringUtils.isBlank(channel)) {
                    setEnable(false);
                    return;
                }
                args.put(StatisticsBase.KEY_TAG, key);
                args.put(StatisticsBase.CHANNEL_TAG, channel);
                break;
            case Onyx:
                final String url = DeviceConfig.sharedInstance(context).getStatisticsUrl();
                args.put(StatisticsBase.STATISTICS_URL, url);
                break;
        }

        statisticsManager.init(context, args, statisticsType);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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
        statisticsManager.onDocumentOpenedEvent(event.getContext(), event.getDocumentInfo());
        updateLastTimestamp();
    }

    @Subscribe
    public void onDocumentClosed(final DocumentCloseEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onDocumentClosed(event.getContext());
    }

    @Subscribe
    public void onActivityPause(final ActivityPauseEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onActivityPause(event.getContext());
    }

    @Subscribe
    public void onActivityResume(final ActivityResumeEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onActivityResume(event.getContext());
    }

    @Subscribe
    public void onPageChanged(final PageChangedEvent event) {
        if (!isEnable()) {
            return;
        }
        event.setDuration((System.currentTimeMillis() - lastTimestamp));
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

    @Subscribe
    public void onNetworkChanged(final NetworkChangedEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onNetworkChangedEvent(event.getContext(), event.isConnected(), event.getNetworkType());
    }

    @Subscribe
    public void onDocumentFinished(final DocumentFinishEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onDocumentFinished(event.getContext(), event.getComment(), event.getScore());
    }

    @Subscribe
    public void onBatteryStatusChange(final BatteryStatusChangeEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onBatteryStatusChange(event.getContext(), event.getStatus(), event.getLevel());
    }
}
