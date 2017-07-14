package com.onyx.android.sdk.im;

/**
 * Created by ming on 2017/7/14.
 */

public class IMInitialState {

    private String applicationId;
    private String clientKey;

    private String serverUri;

    public IMInitialState(String applicationId, String clientKey, String serverUri) {
        this.applicationId = applicationId;
        this.clientKey = clientKey;
        this.serverUri = serverUri;
    }

    public IMInitialState(String applicationId, String clientKey) {
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

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public static IMInitialState create(String applicationId, String clientKey, String serverUri) {
        return new IMInitialState(applicationId, clientKey, serverUri);
    }
}
