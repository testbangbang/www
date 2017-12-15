package com.onyx.jdread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.event.ModifyLibraryDataEvent;
import com.onyx.jdread.library.action.ModifyLibraryDataAction;
import com.onyx.jdread.library.model.DataBundle;

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

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(JDReadApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        instance = this;
        DataManager.init(instance, null);
        PreferenceManager.init(instance);
        initEventListener();
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

    public void dealWithMptBuffer() {
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
}
