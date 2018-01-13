package com.onyx.jdread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.evernote.client.android.EvernoteSession;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.library.action.ModifyLibraryDataAction;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.main.common.ManagerActivityUtils;
import com.onyx.jdread.main.event.ModifyLibraryDataEvent;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-12-6.
 */

public class JDReadApplication extends MultiDexApplication {
    private static final String TAG = JDReadApplication.class.getSimpleName();
    private static JDReadApplication instance = null;
    private static DataBundle dataBundle;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private List<String> mtpBuffer = new ArrayList<>();
    private boolean isUserLogin;
    private AppBaseInfo appBaseInfo;
    private JDAppBaseInfo jdAppBaseInfo;
    private EvernoteSession evernoteSession;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(JDReadApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        lockScreen();
    }

    private void lockScreen() {
        String passWord = PreferenceManager.getStringValue(instance, R.string.password_key, "");
        if (StringUtils.isNotBlank(passWord)) {
            ManagerActivityUtils.lockScreen(instance);
        }
    }

    private void initConfig() {
        instance = this;
        DataManager.init(instance, null);
        initContentProvider(this);
        initFrescoLoader();
        PreferenceManager.init(instance);

        OnyxDownloadManager.init(this.getApplicationContext());
        OnyxDownloadManager.getInstance();
        initEventListener();
        initDownloadManager();
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(this);
        OnyxDownloadManager.getInstance();
    }

    private void initEventListener() {
        deviceReceiver.setMtpEventListener(new DeviceReceiver.MtpEventListener() {
            @Override
            public void onMtpEvent(Intent intent) {
                Uri data = intent.getData();
                if (data != null && StringUtils.isNotBlank(data.getPath())) {
                    File file = new File(data.getPath());
                    if (MimeTypeUtils.getDocumentExtension().contains(FileUtils.getFileExtension(file))) {
                        mtpBuffer.add(data.getPath());
                    }
                }
            }
        });

        deviceReceiver.enable(getApplicationContext(), true);
    }

    public static JDReadApplication getInstance() {
        return instance;
    }

    public static DataBundle getDataBundle() {
        if (dataBundle == null) {
            dataBundle = new DataBundle(instance);
        }
        return dataBundle;
    }

    public void dealWithMtpBuffer() {
        if (CollectionUtils.isNullOrEmpty(mtpBuffer)) {
            return;
        }
        final ModifyLibraryDataAction dataAction = new ModifyLibraryDataAction(mtpBuffer);
        dataAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                mtpBuffer.clear();
                getDataBundle().getEventBus().post(new ModifyLibraryDataEvent());
            }
        });
    }

    private void initFrescoLoader() {
        Fresco.initialize(getInstance().getApplicationContext());
    }

    public void setLogin(boolean isUserLogin) {
        this.isUserLogin = isUserLogin;
    }

    public boolean getLogin() {
        return isUserLogin;
    }

    public AppBaseInfo getAppBaseInfo() {
        if (appBaseInfo == null) {
            synchronized (AppBaseInfo.class) {
                if (appBaseInfo == null) {
                    appBaseInfo = new AppBaseInfo();
                }
            }
        }
        return appBaseInfo;
    }

    public JDAppBaseInfo getJDAppBaseInfo() {
        if (jdAppBaseInfo == null) {
            synchronized (JDAppBaseInfo.class) {
                if (jdAppBaseInfo == null) {
                    jdAppBaseInfo = new JDAppBaseInfo();
                }
            }
        }
        return jdAppBaseInfo;
    }

    static public void initContentProvider(final Context context) {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(context);
            FlowManager.init(builder.build());
        } catch (Exception e) {
            if (com.onyx.android.sdk.dataprovider.BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public EvernoteSession getEvernoteSession() {
        return evernoteSession;
    }
}
