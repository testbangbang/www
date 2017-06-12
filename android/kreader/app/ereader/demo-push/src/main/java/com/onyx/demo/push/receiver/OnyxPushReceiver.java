package com.onyx.demo.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.demo.push.BuildConfig;
import com.onyx.demo.push.events.PushLoadFinishedEvent;
import com.onyx.demo.push.manager.PushManager;
import com.onyx.demo.push.model.PushContent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/3/3.
 */
public class OnyxPushReceiver extends BroadcastReceiver {
    private static final String TAG = OnyxPushReceiver.class.getSimpleName();

    public static final String PUSH_ACTION = "com.onyx.demo.push";
    public static final String PUSH_LEAN_CLOUD_CHANNEL = "com.avos.avoscloud.Channel";
    public static final String PUSH_LEAN_CLOUD_DATA = "com.avos.avoscloud.Data";

    public static final String[] PUSH_DATA = new String[]{PUSH_LEAN_CLOUD_DATA};
    public static final String[] PUSH_CHANNEL = new String[]{PUSH_LEAN_CLOUD_CHANNEL};

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (PUSH_ACTION.equals(intent.getAction())) {
            processPushPlatform(intent);
        }
    }

    private void processPushPlatform(Intent intent) {
        for (int i = 0; i < PUSH_DATA.length; i++) {
            String data = intent.getExtras().getString(PUSH_DATA[i]);
            if (StringUtils.isNotBlank(data)) {
                dumpPushMessage(intent.getAction(), intent.getExtras().getString(PUSH_CHANNEL[i]), data);
                processPushBody(data);
                break;
            }
        }
    }

    private void processPushBody(String bodyData) {
        try {
            PushContent product = JSON.parseObject(bodyData, PushContent.class);
            if (product != null) {
                startDownload(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean startDownload(final PushContent product) {
        BaseDownloadTask task = PushManager.downFromCloud(context, product.url, product.name, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                EventBus.getDefault().post(new PushLoadFinishedEvent(product));
                processDownloadedFile(context, product);
            }
        });
        if (task == null) {
            return false;
        }
        OnyxDownloadManager.getInstance().addTask(product.url, task);
        return OnyxDownloadManager.getInstance().startDownload(task) != 0;
    }

    private static void processDownloadedFile(Context context, PushContent product) {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(PushManager.getDownloadFile(product.name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(
                PushManager.getDownloadFile(product.name), Integer.MAX_VALUE, product.interval));
    }

    private void dumpPushMessage(String action, String channel, String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
            Log.d(TAG, data);
        }
    }
}
