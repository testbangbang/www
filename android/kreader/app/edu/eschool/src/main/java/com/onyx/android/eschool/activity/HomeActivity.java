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
import com.onyx.android.eschool.events.GroupSelectEvent;
import com.onyx.android.eschool.model.AppConfig;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.ResourceUtils;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.GenerateAccountInfoRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/15.
 */
public class HomeActivity extends BaseActivity {
    private static boolean checkedOnBootComplete = false;

    private NeoAccountBase currentAccount;

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
        mergeDisplayUpdate();
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

    private void mergeDisplayUpdate() {
        Device.currentDevice().mergeDisplayUpdate(
                DeviceConfig.sharedInstance(getApplicationContext()).getMergeUpdateTimeout(), UpdateMode.GC_CLEAR);
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
        currentAccount = event.getAccount();
        updateAccountInfo(event.getAccount(), StudentPreferenceManager.getCloudGroupSelected(getApplicationContext()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountTokenErrorEvent(AccountTokenErrorEvent errorEvent) {
        StudentAccount.sendUserInfoSettingIntent(HomeActivity.this, getString(R.string.account_un_login));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupSelectEvent(GroupSelectEvent event) {
        updateAccountInfo(currentAccount, event.index);
    }

    private void updateAccountInfo(NeoAccountBase account, int selectGroupIndex) {
        account = switchAccountGroup(account, selectGroupIndex);
        if (account == null) {
            return;
        }
        StudentAccount.sendUserInfoSettingIntent(getApplicationContext(), account);
        updateScreenAccountInfo(account);
    }

    private void updateScreenAccountInfo(NeoAccountBase account) {
        final GenerateAccountInfoRequest generateAccountInfoRequest = new GenerateAccountInfoRequest(account,
                DeviceConfig.sharedInstance(getApplicationContext()).getInfoShowConfig());
        SchoolApp.getSchoolCloudStore().submitRequest(getApplicationContext(), generateAccountInfoRequest, null);
    }

    private NeoAccountBase switchAccountGroup(NeoAccountBase currentAccount, int selectGroupIndex) {
        if (currentAccount == null || CollectionUtils.isNullOrEmpty(currentAccount.groups)) {
            return null;
        }
        if (selectGroupIndex >= CollectionUtils.getSize(currentAccount.groups)) {
            selectGroupIndex--;
        }
        NeoAccountBase account = new NeoAccountBase();
        account.name = currentAccount.getName();
        account.groups = new ArrayList<>();
        account.groups.add(currentAccount.groups.get(selectGroupIndex));
        return account;
    }
}
