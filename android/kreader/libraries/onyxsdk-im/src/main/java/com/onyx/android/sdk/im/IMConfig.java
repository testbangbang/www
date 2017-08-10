package com.onyx.android.sdk.im;

import com.onyx.android.sdk.im.push.PushServiceType;

/**
 * Created by ming on 2017/7/14.
 */

public class IMConfig {

    private PushServiceType pushServiceType = PushServiceType.AVCLOUDPUSH;
    private String applicationId;
    private String clientKey;

    private String serverUri;
    private String socketIOEvent;
    private String socketIoPath;
    private int reconnectLimit = 0;
    private int reconnectInterval = 2000;

    public IMConfig() {
    }

    public IMConfig(String applicationId, String clientKey) {
        this.applicationId = applicationId;
        this.clientKey = clientKey;
    }

    public IMConfig(String serverUri) {
        this.serverUri = serverUri;
    }

    public IMConfig(String applicationId, String clientKey, String serverUri) {
        this.applicationId = applicationId;
        this.clientKey = clientKey;
        this.serverUri = serverUri;
    }

    public PushServiceType getPushServiceType() {
        return pushServiceType;
    }

    public void setPushServiceType(PushServiceType pushServiceType) {
        this.pushServiceType = pushServiceType;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public int getReconnectLimit() {
        return reconnectLimit;
    }

    public void setReconnectLimit(int reconnectLimit) {
        this.reconnectLimit = reconnectLimit;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public String getSocketIOEvent() {
        return socketIOEvent;
    }

    public void setSocketIOEvent(String socketIOEvent) {
        this.socketIOEvent = socketIOEvent;
    }

    public String getSocketIoPath() {
        return socketIoPath;
    }

    public void setSocketIoPath(String socketIoPath) {
        this.socketIoPath = socketIoPath;
    }

    public boolean canReconnect(int reconnectCount) {
        return reconnectCount < this.reconnectLimit;
    }

    public static IMConfig create(String serverUri, String socketIOEvent, String socketIoPath) {
        IMConfig config = new IMConfig();
        config.setServerUri(serverUri);
        config.setSocketIOEvent(socketIOEvent);
        config.setSocketIoPath(socketIoPath);
        return config;
    }
}
