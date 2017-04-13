package com.onyx.kreader.reader.data;

import android.content.Context;

import com.onyx.kreader.reader.handler.HandlerManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ming on 2017/4/13.
 */

public class ReaderDataHolder {

    private EventBus eventBus = new EventBus();

    private Context context;

    private HandlerManager handlerManager;

    public ReaderDataHolder(Context context) {
        this.context = context;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void registerEventBus(final Object subscriber) {
        getEventBus().register(subscriber);
    }

    public void unRegisterEventBus(final Object subscriber) {
        getEventBus().unregister(subscriber);
    }

    public Context getContext() {
        return context;
    }

    public final HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }
}
