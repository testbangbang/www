package com.onyx.kreader.ui.events;


import com.onyx.android.sdk.statistics.StatisticsManager;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class EventReceiver {

    private boolean enable = true;
    private StatisticsManager statisticsManager = new StatisticsManager();

    public EventReceiver() {
    }

    public boolean isEnable() {
        return enable;
    }

    @Subscribe
    public void onDocumentOpened(final DocumentOpenEvent event) {
        if (!isEnable()) {
            return;
        }
        statisticsManager.onDocumentOpenedEvent(event.getContext(), event.getPath(), event.getMd5());
    }

    @Subscribe
    public void onPageChanged(final PageChangedEvent event) {
        if (!isEnable()) {
            return;
        }
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
