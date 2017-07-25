package com.onyx.einfo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.AuthTokenAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.manager.PushActionManager;

import java.util.Arrays;

/**
 * Created by suicheng on 2017/6/27.
 */

public class OnyxPushReceiver extends BroadcastReceiver {
    public static final String PUSH_LEAN_CLOUD_CHANNEL = "com.avos.avoscloud.Channel";
    public static final String PUSH_LEAN_CLOUD_DATA = "com.avos.avoscloud.Data";

    public static final String[] PUSH_DATA = new String[]{PUSH_LEAN_CLOUD_DATA};
    public static final String[] PUSH_CHANNEL = new String[]{PUSH_LEAN_CLOUD_CHANNEL};

    private static PushActionManager pushActionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        getPushManager().processPushAction(context, intent);
    }

    private static PushActionManager getPushManager() {
        if (pushActionManager == null) {
            pushActionManager = new PushActionManager();
            PushActionManager.PUSH_DATA.addAll(Arrays.asList(PUSH_DATA));
            pushActionManager.addAction("com.onyx.push", CustomPushAction.class);
            return pushActionManager;
        }
        return pushActionManager;
    }

    public static class CustomPushAction extends PushActionManager.PushAction {

        public CustomPushAction() {
        }

        @Override
        public void execute(PushActionManager.PushActionContext actionContext) {
            AuthTokenAction authTokenAction = new AuthTokenAction();
            authTokenAction.execute(InfoApp.getLibraryDataHolder(), new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    Log.e("##reLoginPushAction", String.valueOf("success?") + String.valueOf(e == null));
                }
            });
        }
    }
}
