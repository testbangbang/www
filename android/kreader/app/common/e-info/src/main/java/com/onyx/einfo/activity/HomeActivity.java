package com.onyx.einfo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.AuthTokenAction;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.einfo.utils.UniversalViewUtils;

/**
 * Created by suicheng on 2016/11/15.
 */
public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final View view = findViewById(android.R.id.content);
        loadAuthToken(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EpdController.invalidate(view, UpdateMode.GC);
            }
        });
    }

    @Override
    protected Integer getLayoutId() {
        return getLayoutIdByConfig();
    }

    private int getLayoutIdByConfig() {
        return R.layout.activity_home_for_display;
    }

    @Override
    protected void initView() {
        initNormalItem();
    }

    @Override
    protected void initData() {
    }

    private void loadAuthToken(final BaseCallback callback) {
        AuthTokenAction authTokenAction = new AuthTokenAction();
        if (!isCheckOnBootComplete()) {
            setCheckedOnBootComplete();
            authTokenAction.setLocalLoadRetryCount(3);
        }
        authTokenAction.execute(InfoApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private static boolean isCheckOnBootComplete() {
        return InfoApp.checkedOnBootComplete;
    }

    private static void setCheckedOnBootComplete() {
        InfoApp.checkedOnBootComplete = true;
    }

    protected void initConfig() {
    }

    private void initNormalItem() {
        initNormalView(R.id.home_main_item, R.string.app_name, R.drawable.home_eschool,
                new Intent(this, MainActivity.class));
        initNormalView(R.id.home_application_item, R.string.home_item_application_text, R.drawable.home_applicaiton,
                new Intent(this, ApplicationsActivity.class));
        initNormalView(R.id.home_setting_item, R.string.home_item_setting_text, R.drawable.home_setting,
                getSettingIntent());
        initNormalView(R.id.home_library_item, R.string.home_item_library, R.drawable.home_syllabus,
                new Intent(this, LibraryActivity.class));
    }

    private void initNormalView(int layoutResId, int textResId, int imageResId, final Intent intent) {
        UniversalViewUtils.initNormalView(this, layoutResId, textResId, imageResId, intent);
    }

    private Intent getSettingIntent() {
        return ConfigPreferenceManager.getSettingsIntent();
    }
}
