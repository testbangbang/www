package com.onyx.edu.reader.ui.events.im;

import android.content.Context;

import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.data.JoinModel;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.im.event.MessageEvent;
import com.onyx.android.sdk.im.socket.SocketIOClient;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by ming on 2017/8/8.
 */

public class IMAdapter {

    private ReaderDataHolder readerDataHolder;
    private boolean started = false;

    public IMAdapter(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        getEventBus().register(this);
    }

    public EventBus getEventBus() {
        return getIMManager().getEventBus();
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    @Subscribe
    public void onStartSocketIOEvent(StartSocketIOEvent event) {
        if (event == null) {
            return;
        }
        if (isStarted()) {
            return;
        }
        getIMManager().init(event.getConfig()).startSocketService(getContext());
        setStarted(true);
    }

    @Subscribe
    public void onEmitMessageEvent(EmitMessageEvent event) {
        if (event == null) {
            return;
        }
        getSocketIOClient().emit(event.getMessage().getEvent(), JSONObjectParseUtils.toJson(event.getMessage()));
    }

    @Subscribe
    public void onCloseSocketIOEvent(CloseSocketIOEvent event) {
        if (event == null) {
            return;
        }
        setStarted(false);
        getIMManager().close(getContext());
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    private IMManager getIMManager(){
        return IMManager.getInstance();
    }

    private SocketIOClient getSocketIOClient() {
        return getIMManager().getSocketIOClient();
    }

    @Subscribe
    public void onReceivedMessage(MessageEvent event){
        if (event == null) {
            return;
        }
        getReaderDataHolder().getHandlerManager().getActiveProvider().onReceivedIMMessage(event.message);
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    public Context getContext() {
        return getReaderDataHolder().getContext();
    }
}
