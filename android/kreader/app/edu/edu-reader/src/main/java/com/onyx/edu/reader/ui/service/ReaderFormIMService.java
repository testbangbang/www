package com.onyx.edu.reader.ui.service;

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

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


/**
 * Created by ming on 2017/8/8.
 */

public class ReaderFormIMService {

    public interface Callback {
        void onReceivedMessage(Message message);
    }

    private IMConfig imConfig;
    private Callback callback;
    private Context context;
    private boolean enable = true;
    private boolean hasStart = false;

    public ReaderFormIMService(Context context, IMConfig imConfig) {
        this.imConfig = imConfig;
        this.context = context;
        init();
    }

    private void init(){
        getIMManager().init(imConfig);
        getIMManager().getEventBus().register(this);
        setEnable(true);
    }

    public void startSocketIO(Context context) {
        if (!isEnable()) {
            return;
        }
        // only start one time
        if (hasStart) {
            return;
        }
        getIMManager().startSocketService(context);
        hasStart = true;
    }

    public void startPush(Context context) {
        if (!isEnable()) {
            return;
        }
        getIMManager().startPushService(context);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private IMManager getIMManager(){
        return IMManager.getInstance();
    }

    private SocketIOClient getSocketIOClient() {
        return getIMManager().getSocketIOClient();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Subscribe
    public void onReceivedMessage(MessageEvent event){
        if (event == null) {
            return;
        }
        handleReceivedMessage(event.message);
        if (callback != null) {
            callback.onReceivedMessage(event.message);
        }
    }

    public void emit(Message message) {
        if (!isEnable()) {
            return;
        }
        getSocketIOClient().emit(getEvent(), JSONObjectParseUtils.toJson(message));
    }

    public void joinRoom(JoinModel joinModel) {
        if (!isEnable()) {
            return;
        }
        Debug.d(getClass(), "vote message: join name " + joinModel.getName());
        getSocketIOClient().emit(Constant.EVENT_JOIN, JSONObjectParseUtils.toJson(joinModel));
    }

    private String getEvent() {
        return imConfig.getSocketIOEvent();
    }

    private void handleReceivedMessage(Message message) {
        String action = message.getAction();
        switch (action) {
            case Constant.EVENT_JOIN:
                break;
            case Constant.EVENT_NEW_USER:
                break;
        }
    }

    public Context getContext() {
        return context;
    }

    public void close() {
        setEnable(false);
        getIMManager().close(getContext());
        getIMManager().getEventBus().unregister(this);
    }
}
