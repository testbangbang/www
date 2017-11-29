package com.onyx.android.eschool.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.AuthTokenAction;
import com.onyx.android.eschool.action.ContentImportAction;
import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.model.AppConfig;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.ResourceUtils;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/15.
 */
public class HomeActivity extends BaseActivity {
    private static boolean checkedOnBootComplete = false;
    private long lastGcTs = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        postCreate();
    }

    private void postCreate() {
        cloudContentImportFirstBoot();
    }

    private void cloudContentImportFirstBoot() {
        if (isCheckOnBootComplete() || StudentPreferenceManager.hasImportContent(this)) {
            return;
        }
        String jsonFilePath = DeviceConfig.sharedInstance(getApplicationContext()).getCloudContentImportJsonFilePath();
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        File file = new File(EnvironmentUtil.getExternalStorageDirectory(), jsonFilePath);
        if (!file.exists()) {
            return;
        }
        ContentImportAction importAction = new ContentImportAction(file.getAbsolutePath());
        importAction.execute(SchoolApp.getLibraryDataHolder(), new BaseCallback() {

            @Override
            public void start(BaseRequest request) {
                showProgressDialog(request, R.string.cloud_content_import_loading, null);
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(request);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final View view = findViewById(android.R.id.content);
        loadAuthToken(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (DeviceConfig.sharedInstance(getApplicationContext()).enableFullRefresh()) {
                    EpdController.invalidate(view, UpdateMode.GC);
                }
            }
        });
    }

    @Override
    protected Integer getLayoutId() {
        return getLayoutIdByConfig();
    }

    private int getLayoutIdByConfig() {
        AppConfig config = AppConfig.sharedInstance(this);
        int layoutId = ResourceUtils.getLayoutResIdByName(this, config.getHomeLayout());
        return layoutId <= 0 ? R.layout.activity_home_for_display : layoutId;
    }

    @Override
    protected void initView() {
        initNormalItem();
        initViewByConfig();
    }

    protected void initViewByConfig() {
        if (AppConfig.sharedInstance(this).isForDisplayHomeLayout()) {
            initDisplayItemView();
        }
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
        authTokenAction.execute(SchoolApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private static boolean isCheckOnBootComplete() {
        return checkedOnBootComplete;
    }

    private static void setCheckedOnBootComplete() {
        checkedOnBootComplete = true;
    }

    protected void initConfig() {
    }

    private void initNormalItem() {
        initNormalView(R.id.home_dictionary_item, R.string.home_item_application_text, R.drawable.home_application, getApplicationListIntent());
        initNormalView(R.id.home_setting_item, R.string.home_item_setting_text, R.drawable.home_setting, getSettingIntent());
    }

    private void initDisplayItemView() {
        initNormalView(R.id.home_pic_display_item, R.string.home_item_syllabus, R.drawable.home_syllabus,
                new Intent(this, SyllabusActivity.class));
    }

    private void initNormalView(int layoutResId, int textResId, int imageResId, final Intent intent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(layoutResId);
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_category_text);
        textView.setText(textResId);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imageView_category_image);
        imageView.setImageResource(imageResId);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNormalViewClick(intent);
            }
        });
    }

    private void processNormalViewClick(Intent intent) {
        ActivityUtil.startActivitySafely(this, intent);
    }

    @OnClick(R.id.home_main_item)
    void onTeachingMaterialClick() {
        //ActivityUtil.startActivitySafely(this, getPackageManager().getLaunchIntentForPackage("com.youngy.ui"));
        ActivityUtil.startActivitySafely(this, new Intent(this, MainActivity.class));
    }

    private Intent getApplicationListIntent() {
        return new Intent(this, ApplicationsActivity.class);
    }

    private Intent getSettingIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.onyx.android.settings",
                "com.onyx.android.libsetting.view.activity.DeviceMainSettingActivity"));
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAvailableEvent(AccountAvailableEvent event) {
        StudentAccount.sendUserInfoSettingIntent(HomeActivity.this, event.getAccount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountTokenErrorEvent(AccountTokenErrorEvent errorEvent) {
        StudentAccount.sendUserInfoSettingIntent(HomeActivity.this, getString(R.string.account_un_login));
    }

    // Back button is a no-op here
    @Override
    public void onBackPressed() {
        processBackPressedFullUpdate();
    }

    private void processBackPressedFullUpdate() {
        if (System.currentTimeMillis() - lastGcTs >= DeviceConfig.sharedInstance(this).gcIgnoreInterval()) {
            EpdController.invalidate(findViewById(android.R.id.content), UpdateMode.GC);
            lastGcTs = System.currentTimeMillis();
        }
    }
}
