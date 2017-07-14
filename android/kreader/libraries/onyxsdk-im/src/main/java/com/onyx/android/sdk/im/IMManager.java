package com.onyx.android.sdk.im;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.im.push.AVOSCloudPushService;
import com.onyx.android.sdk.im.push.BasePushService;
import com.onyx.android.sdk.im.push.PushServiceType;
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
    private IMInitArgs imInitArgs;
    private EventBus eventBus = new EventBus();

    public static IMManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new IMManager();
        }
        return ourInstance;
    }

    public IMManager() {
    }

    public IMManager start(IMInitArgs imInitArgs) {
        this.imInitArgs = imInitArgs;
        return this;
    }

    public void close(Context context) {
        disablePushService(context);
        disableSocketService(context);
        messageIdSets.clear();
    }

    public IMManager enablePushService(Context activityContext, PushServiceType type) {
        this.pushEnable = true;
        startPushService(activityContext, type);
        return this;
    }

    public IMManager disablePushService(Context activityContext) {
        this.pushEnable = false;
        stopPushService(activityContext);
        return this;
    }

    public IMManager enableSocketService(Context context) {
        this.socketEnable = true;
        startSocketIOClient(context);
        return this;
    }

    public IMManager disableSocketService(Context context) {
        this.socketEnable = false;
        stopSocketIOClient(context);
        return this;
    }

    private void startSocketIOClient(Context context) {
        if (imInitArgs == null) {
            return;
        }
        String serverUri = imInitArgs.getServerUri();
        if (StringUtils.isNullOrEmpty(serverUri)) {
            return;
        }
        socketIOClient = new SocketIOClient(imInitArgs.getConfig(), serverUri);
        socketIOClient.connect();
        socketIOClient.on(Constant.PUSH, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Debug.d(getClass(), "receiver push");
                if (args.length > 0) {
                    JSONObject data = (JSONObject) args[0];
                    Message message = JSONObject.parseObject(data.toJSONString(), Message.class);
                    forwardSocketMessage(message);
                }
            }
        });
    }

    private void stopSocketIOClient(Context context) {
        if (socketIOClient == null) {
            return;
        }
        socketIOClient.off(Constant.PUSH, null);
        socketIOClient.close();
        socketIOClient = null;
    }

    private void startPushService(Context activityContext, PushServiceType type) {
        if (imInitArgs == null) {
            return;
        }
        switch (type) {
            case AVCLOUDPUSH:
                basePushService = new AVOSCloudPushService();
                break;
            default:
                basePushService = new AVOSCloudPushService();
        }
        basePushService.init(activityContext, imInitArgs);
        basePushService.start(activityContext);
    }

    private void stopPushService(Context activityContext) {
        basePushService.stop(activityContext);
        basePushService = null;
    }

    public void forwardPushMessage(Message message) {
        if (!pushEnable) {
            return;
        }
        forwardMessage(message);
    }

    public void forwardSocketMessage(Message message) {
        if (!socketEnable) {
            return;
        }
        forwardMessage(message);
    }

    public void forwardMessage(Message message) {
        String messageId = message.getId();
        if (messageIdSets.contains(messageId)) {
            return;
        }
        messageIdSets.add(messageId);
        getEventBus().post(MessageEvent.create(message));
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
