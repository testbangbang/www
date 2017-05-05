package com.onyx.kreader.reader.handler;

import com.onyx.kreader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/13.
 */

public abstract class BaseHandler {

    private HandlerManager handlerManager;

    public BaseHandler(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    public void onActivate(final ReaderDataHolder readerDataHolder) {}

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {}
}
