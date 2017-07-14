package com.onyx.android.sdk.im;

import com.onyx.android.sdk.im.socket.SocketIOConfig;

/**
 * Created by ming on 2017/7/14.
 */

public class IMInitArgs {

    private String applicationId;
    private String clientKey;

    private String serverUri;
    private SocketIOConfig config;

    public IMInitArgs(String applicationId, String clientKey, String serverUri, SocketIOConfig config) {
        this.applicationId = applicationId;
        this.clientKey = clientKey;
        this.serverUri = serverUri;
        this.config = config;
    }

    public IMInitArgs(String applicationId, String clientKey) {
        this.applicationId = applicationId;
        this.clientKey = clientKey;
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

    public SocketIOConfig getConfig() {
        return config;
    }

    public void setConfig(SocketIOConfig config) {
        this.config = config;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public static IMInitArgs create(String applicationId, String clientKey, String serverUri, SocketIOConfig config) {
        return new IMInitArgs(applicationId, clientKey, serverUri, config);
    }
}
