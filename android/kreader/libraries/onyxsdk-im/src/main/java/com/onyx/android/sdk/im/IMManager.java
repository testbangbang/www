package com.onyx.android.sdk.im;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.im.event.MessageEvent;
import com.onyx.android.sdk.im.push.AVOSCloudPushService;
import com.onyx.android.sdk.im.push.BasePushService;
import com.onyx.android.sdk.im.socket.SocketIOClient;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import io.socket.emitter.Emitter;

/**
 * Created by ming on 2017/7/12.
 */

public class IMManager {

    private static final String TAG = "IMManager";

    private static IMManager ourInstance;

    private SocketIOClient socketIOClient;
    private BasePushService basePushService;

    private boolean pushEnable = false;
    private boolean socketEnable = false;
    private Set<String> messageIdSets = new HashSet<>();
    private IMConfig config;
    private EventBus eventBus = new EventBus();

    public static IMManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new IMManager();
        }
        return ourInstance;
    }

    public IMManager() {
    }

    public IMManager init(IMConfig imConfig) {
        this.config = imConfig;
        return this;
    }

    public void close(Context context) {
        stopPushService(context);
        stopSocketService(context);
        messageIdSets.clear();
    }

    public IMManager startPushService(Context activityContext) {
        this.pushEnable = true;
        startPushServiceImpl(activityContext);
        return this;
    }

    public IMManager stopPushService(Context activityContext) {
        this.pushEnable = false;
        stopPushServiceImpl(activityContext);
        return this;
    }

    public IMManager startSocketService(Context context) {
        this.socketEnable = true;
        startSocketIOClient(context);
        return this;
    }

    public IMManager stopSocketService(Context context) {
        this.socketEnable = false;
        stopSocketIOClient(context);
        return this;
    }

    private void startSocketIOClient(Context context) {
        if (config == null || StringUtils.isNullOrEmpty(config.getServerUri())) {
            return;
        }
        String event = config.getSocketIOEvent();
        if (StringUtils.isNullOrEmpty(event)) {
            return;
        }
        socketIOClient = getSocketIOClient(config);
        socketIOClient.connect();
        socketIOClient.on(event, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Debug.d(getClass(), "receiver push");
                if (args.length > 0) {
                    String data = (String) args[0];
                    Message message = JSONObject.parseObject(data, Message.class);
                    onReceivedSocketMessage(message);
                }
            }
        });
    }

    private SocketIOClient getSocketIOClient(IMConfig c) {
        if (socketIOClient == null) {
            socketIOClient = new SocketIOClient(this, c);
        }
        socketIOClient.setConfig(c);
        return socketIOClient;
    }

    private void stopSocketIOClient(Context context) {
        if (socketIOClient == null) {
            return;
        }
        socketIOClient.close();
        socketIOClient = null;
    }

    private void startPushServiceImpl(Context appContext) {
        if (config == null) {
            return;
        }
        switch (config.getPushServiceType()) {
            case AVCLOUDPUSH:
                basePushService = new AVOSCloudPushService();
                break;
            default:
                basePushService = new AVOSCloudPushService();
                break;
        }
        basePushService.init(appContext, config);
        basePushService.start(appContext);
    }

    private void stopPushServiceImpl(Context activityContext) {
        if (basePushService == null) {
            return;
        }
        basePushService.stop(activityContext);
        basePushService = null;
    }

    public void onReceivedPushMessage(Message message) {
        if (!pushEnable) {
            return;
        }
        broadcastMessage(message);
    }

    public void onReceivedSocketMessage(Message message) {
        if (!socketEnable) {
            return;
        }
        broadcastMessage(message);
    }

    public void broadcastMessage(Message message) {
        String messageId = message.getId();
        if (!StringUtils.isNullOrEmpty(messageId) && messageIdSets.contains(messageId)) {
            return;
        }
        if (!StringUtils.isNullOrEmpty(messageId)) {
            messageIdSets.add(messageId);
        }
        getEventBus().post(MessageEvent.create(message));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SocketIOClient getSocketIOClient() {
        return socketIOClient;
    }

    public Set<String> getMessageIdSets() {
        return messageIdSets;
    }
}
