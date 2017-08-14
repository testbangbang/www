package com.onyx.demo.push;

import android.content.Context;

import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.action.push.DownloadAction;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.im.event.MessageEvent;
import com.onyx.android.sdk.im.push.LeanCloudManager;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.demo.push.activity.MainActivity;
import com.onyx.demo.push.events.PushLoadFinishedEvent;
import com.onyx.demo.push.manager.PushManager;
import com.onyx.demo.push.model.PushContent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by suicheng on 2017/3/3.
 */
public class ReadingApplication extends MultiDexApplication {

    static private ReadingApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ReadingApplication.this);
    }

    @Override
    public void onTerminate() {
        IMManager.getInstance().getEventBus().unregister(this);
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            initLeanCloud();
            initDownloadManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLeanCloud() {
        String appId = "M6M8NRsGaGk4gto7I5HUnUHA-gzGzoHsz";
        String appKey = "7LBuY74znzTeuJk11Kb3YBUL";
        IMManager.getInstance().init(new IMConfig(appId, appKey)).startPushService(getApplicationContext());
        IMManager.getInstance().getEventBus().register(this);
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(sInstance.getApplicationContext());
        OnyxDownloadManager.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMessageEvent(MessageEvent messageEvent) {
        if (messageEvent.message == null) {
            return;
        }
        processPushContent(JSONObjectParseUtils.parseObject(messageEvent.message.getContent(), PushContent.class));
    }

    private void processPushContent(final PushContent product) {
        if (product == null) {
            return;
        }
        File file = PushManager.getDownloadFile(product.name);
        DownloadAction downloadAction = new DownloadAction(product.url,
                file == null ? null : file.getAbsolutePath(), product.url, null);
        downloadAction.execute(ActionContext.create(getApplicationContext(), new CloudManager(), new DataManager()),
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        EventBus.getDefault().post(new PushLoadFinishedEvent(product));
                        processDownloadedFile(getApplicationContext(), product);
                    }
                });
    }

    private void processDownloadedFile(Context context, PushContent product) {
        File file = PushManager.getDownloadFile(product.name);
        ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(file,
                Integer.MAX_VALUE, product.interval));
    }
}
