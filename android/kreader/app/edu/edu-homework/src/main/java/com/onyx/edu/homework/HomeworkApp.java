package com.onyx.edu.homework;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.data.BuglyConfig;
import com.onyx.edu.homework.data.EduProxy;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.HomeworkGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.tencent.bugly.crashreport.CrashReport;

import java.net.InetSocketAddress;

import me.yokeyword.fragmentation.Fragmentation;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkApp extends MultiDexApplication {

    public static HomeworkApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BaseNoteAction.setAppContext(this);
        initContentProvider(this);
        initDataProvider();
        initCompatColorImageConfig();
        Debug.setDebug(BuildConfig.DEBUG || DeviceUtils.isEngVersion() || PackageUtils.getAppType(this).equals(PackageUtils.APP_TYPE_DEBUG));
        ServiceFactory.setOpenProxy(EduProxy.openProxyDebug);
        if (EduProxy.openProxyDebug) {
            ServiceFactory.setInetSocketAddress(new InetSocketAddress(EduProxy.HOST, EduProxy.PORT));
        }

        // fragment
        Fragmentation.builder()
                     .stackViewMode(Fragmentation.BUBBLE)
                     .debug(BuildConfig.DEBUG)
                     .install();

        CrashReport.setIsDevelopmentDevice(getApplicationContext(), BuildConfig.DEBUG);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        if (BuildConfig.DEBUG) {
            strategy.setAppChannel("debug device");
        }
        CrashReport.initCrashReport(getApplicationContext(), BuglyConfig.APP_ID, BuildConfig.DEBUG, strategy);
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

    public void initDataProvider() {
        FlowConfig.Builder builder = new FlowConfig.Builder(this);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        builder.addDatabaseHolder(HomeworkGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }

    private void initCompatColorImageConfig() {
        AppCompatImageViewCollection.setAlignView(true);
    }
}
