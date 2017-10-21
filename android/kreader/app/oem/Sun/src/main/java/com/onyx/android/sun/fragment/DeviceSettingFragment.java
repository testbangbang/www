package com.onyx.android.sun.fragment;

import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.adapter.DeviceSettingAdapter;
import com.onyx.android.sun.adapter.DeviceSettingAutomaticShutdownAdapter;
import com.onyx.android.sun.adapter.DeviceSettingDeviceInformationAdapter;
import com.onyx.android.sun.adapter.DeviceSettingDeviceStorageInformationAdapter;
import com.onyx.android.sun.adapter.DeviceSettingLanguageSettingsAdapter;
import com.onyx.android.sun.adapter.DeviceSettingLockScreenTimeAdapter;
import com.onyx.android.sun.adapter.DeviceSettingSystemUpdateAdapter;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.common.ManagerActivityUtils;
import com.onyx.android.sun.data.database.DeviceVersionEntity;
import com.onyx.android.sun.databinding.DeviceSettingFragmentBinding;
import com.onyx.android.sun.devicesetting.SystemLanguage;
import com.onyx.android.sun.event.DeviceSettingViewBaseEvent;
import com.onyx.android.sun.event.LoadConfigDataEvent;
import com.onyx.android.sun.event.ToMainFragmentEvent;
import com.onyx.android.sun.interfaces.DeviceSettingView;
import com.onyx.android.sun.presenter.DeviceSettingPresenter;
import com.onyx.android.sun.utils.ApkUtils;
import com.onyx.android.sun.utils.LocalPackageUpdate;
import com.onyx.android.sun.utils.NetworkUtil;
import com.onyx.android.sun.utils.SystemUtils;
import com.onyx.android.sun.utils.TimeUtils;
import com.onyx.android.sun.view.CustomDialog;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;
import com.onyx.android.sun.view.PageRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by huxiaomao on 2016/12/12.
 */

public class DeviceSettingFragment extends BaseFragment implements DeviceSettingView, View.OnClickListener {
    private static final String TAG = DeviceSettingFragment.class.getSimpleName();
    private LinearLayout settingPageView;
    private PageRecyclerView settingRecyclerView;
    private DeviceSettingPresenter deviceSettingPresenter;
    private DeviceSettingAdapter deviceSettingAdapter;

    private RelativeLayout pagerRefreshView;
    private PageRecyclerView pageRefreshRecyclerView;
    private TextView pageRefreshSave = null;

    private RelativeLayout lockScreenTimeView;
    private PageRecyclerView lockScreenTimeRecyclerView;
    private DeviceSettingLockScreenTimeAdapter deviceSettingLockScreenTimeAdapter;
    private TextView lockScreenTimeSave = null;

    private RelativeLayout automaticShutdownView;
    private PageRecyclerView automaticShutdownRecyclerView;
    private DeviceSettingAutomaticShutdownAdapter deviceSettingAutomaticShutdownAdapter;
    private TextView automaticShutdownSave = null;

    private RelativeLayout languageSettingsView;
    private PageRecyclerView languageSettingsRecyclerView;
    private DeviceSettingLanguageSettingsAdapter deviceSettingLanguageSettingsAdapter;
    private TextView languageSettingsSave = null;
    private TextView languageSettingsTitle = null;

    private TextView deviceInformationTitle;
    private LinearLayout deviceInformationView;
    private PageRecyclerView deviceInformationRecyclerView;
    private DeviceSettingDeviceInformationAdapter deviceSettingDeviceInformationAdapter;

    private LinearLayout deviceStorageInformationView;
    private PageRecyclerView deviceStorageInformationRecyclerView;
    private DeviceSettingDeviceStorageInformationAdapter deviceSettingDeviceStorageInformationAdapter;

    private LinearLayout systemUpdateView;
    private PageRecyclerView systemUpdateRecyclerView;
    private DeviceSettingSystemUpdateAdapter deviceSettingSystemUpdateAdapter;
    private TextView systemUpdateCheckUpdate;

    private int currentPageID = R.id.setting_page;
    private int reference;
    private String path;
    private String apkName;
    private long lastPressTime;
    private int resetPressCount;

    private long deviceInformationTitleLastPressTime;
    private long deviceInformationTitleResetPressCount;
    private DeviceSettingFragmentBinding settingBinding;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(DeviceSettingFragment.class.getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(DeviceSettingFragment.class.getSimpleName());
    }

    @Override
    protected void initListener() {
        if (deviceSettingSystemUpdateAdapter == null) {
            return;
        }

        deviceSettingSystemUpdateAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                String tag = (String) view.getTag();
                if (getString(R.string.system_update).equals(tag)) {
                    onCheckSystemUpdateClick(view);
                } else if (getString(R.string.bookstore_update).equals(tag)) {
                    onCheckAPKUpdateClick();
                }
            }
        });
    }

    private void onCheckAPKUpdateClick() {
        CommonNotices.show(getString(R.string.device_setting_check_system_update));
        if (!NetworkUtil.isNetworkConnected(SunApplication.getInstance())) {
            NetworkUtil.toggleWiFi(SunApplication.getInstance(), true);
            return;
        }
        ApkUtils.updateApk(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadConfigDataEvent(LoadConfigDataEvent event) {
        initDeviceStorageInformationView(settingBinding.getRoot());
        initListener();
    }

    private void initSettingListPageView(View rootView) {
        settingPageView = (LinearLayout) rootView.findViewById(R.id.setting_page);
        deviceSettingAdapter = new DeviceSettingAdapter(getActivity());
        deviceSettingAdapter.setTitles(deviceSettingPresenter.getDeviceSettingTitle());
        deviceSettingAdapter.setValues(deviceSettingPresenter.getDeviceSettingValue(getActivity()));
        settingRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.setting_recycler_view);
        settingRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        settingRecyclerView.addItemDecoration(dividerItemDecoration);
        settingRecyclerView.setAdapter(deviceSettingAdapter);
    }

    private void initLockScreenTimeView(View rootView) {
        deviceSettingLockScreenTimeAdapter = new DeviceSettingLockScreenTimeAdapter(getActivity());
        deviceSettingLockScreenTimeAdapter.setTimes(deviceSettingPresenter.getDeviceSettingLockScreenTime(),
                deviceSettingPresenter.getDeviceSettingLockScreenTimeValue(),
                deviceSettingPresenter.getCurrentScreenTimeout());
        lockScreenTimeView = (RelativeLayout) rootView.findViewById(R.id.setting_lock_screen_time);

        lockScreenTimeRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.lock_screen_time_recycler_view);
        lockScreenTimeRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        lockScreenTimeRecyclerView.addItemDecoration(dividerItemDecoration);
        lockScreenTimeRecyclerView.setAdapter(deviceSettingLockScreenTimeAdapter);

        lockScreenTimeSave = (TextView) rootView.findViewById(R.id.lock_screen_time_save);
        lockScreenTimeSave.setOnClickListener(this);
    }

    private void initAutomaticShutdownView(View rootView) {
        deviceSettingAutomaticShutdownAdapter = new DeviceSettingAutomaticShutdownAdapter(getActivity());
        deviceSettingAutomaticShutdownAdapter.setAutomaticShutdownTimes(deviceSettingPresenter.getDeviceSettingAutomaticShutdownTime(),
                deviceSettingPresenter.getDeviceSettingAutomaticShutdownTimeValue(),
                deviceSettingPresenter.getDeviceSettingAutomaticShutdownTimeExplain(),
                deviceSettingPresenter.getCurrentTimeoutValue());
        automaticShutdownView = (RelativeLayout) rootView.findViewById(R.id.setting_automatic_shutdown);

        automaticShutdownRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.automatic_shutdown_recycler_view);
        automaticShutdownRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        automaticShutdownRecyclerView.addItemDecoration(dividerItemDecoration);
        automaticShutdownRecyclerView.setAdapter(deviceSettingAutomaticShutdownAdapter);

        automaticShutdownSave = (TextView) rootView.findViewById(R.id.automatic_shutdown_save);
        automaticShutdownSave.setOnClickListener(this);
    }

    private void initLanguageSettingsView(View rootView) {
        deviceSettingLanguageSettingsAdapter = new DeviceSettingLanguageSettingsAdapter(getActivity());
        deviceSettingLanguageSettingsAdapter.setLanguages(deviceSettingPresenter.getLocaleLanguageInfoList());
        languageSettingsView = (RelativeLayout) rootView.findViewById(R.id.setting_language_settings);

        languageSettingsRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.language_settings_recycler_view);
        languageSettingsRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        languageSettingsRecyclerView.addItemDecoration(dividerItemDecoration);
        languageSettingsRecyclerView.setAdapter(deviceSettingLanguageSettingsAdapter);

        languageSettingsSave = (TextView) rootView.findViewById(R.id.language_settings_save);
        languageSettingsSave.setOnClickListener(this);

        languageSettingsTitle = (TextView) rootView.findViewById(R.id.language_settings_title);
        setLanguageCurrentName(deviceSettingPresenter.getLocaleLanguageInfoList().currentLanguage);
    }

    private void initDeviceInformationView(View rootView) {
        deviceSettingDeviceInformationAdapter = new DeviceSettingDeviceInformationAdapter(getActivity());
        deviceSettingDeviceInformationAdapter.setDeviceInformation(deviceSettingPresenter.getDeviceSettingDeviceInformation());
        deviceInformationView = (LinearLayout) rootView.findViewById(R.id.setting_device_information);
        deviceInformationTitle = (TextView) rootView.findViewById(R.id.device_information_title);
        deviceInformationTitle.setOnClickListener(this);

        deviceInformationRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.device_information_recycler_view);
        deviceInformationRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        deviceInformationRecyclerView.addItemDecoration(dividerItemDecoration);
        deviceInformationRecyclerView.setAdapter(deviceSettingDeviceInformationAdapter);
    }

    private void initDeviceStorageInformationView(View rootView) {
        deviceSettingDeviceStorageInformationAdapter = new DeviceSettingDeviceStorageInformationAdapter(getActivity());
        deviceSettingDeviceStorageInformationAdapter.setDeviceStorageInformations(deviceSettingPresenter.getDeviceStorageInformationList());
        deviceStorageInformationView = (LinearLayout) rootView.findViewById(R.id.setting_device_storage_information);

        deviceStorageInformationRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.device_storage_information_recycler_view);
        deviceStorageInformationRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        deviceStorageInformationRecyclerView.addItemDecoration(dividerItemDecoration);
        deviceStorageInformationRecyclerView.setAdapter(deviceSettingDeviceStorageInformationAdapter);
    }

    private void initSystemUpdateView(View rootView) {
        deviceSettingSystemUpdateAdapter = new DeviceSettingSystemUpdateAdapter(getActivity());
        deviceSettingSystemUpdateAdapter.setSystemVersionInformationList(deviceSettingPresenter.getSystemVersionInformationList());
        systemUpdateView = (LinearLayout) rootView.findViewById(R.id.setting_system_update);

        systemUpdateRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.system_update_recycler_view);
        systemUpdateRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        systemUpdateRecyclerView.addItemDecoration(dividerItemDecoration);
        systemUpdateRecyclerView.setAdapter(deviceSettingSystemUpdateAdapter);

        systemUpdateCheckUpdate = (TextView) rootView.findViewById(R.id.system_update_check_update);
        systemUpdateCheckUpdate.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLockScreenTimeEvent(DeviceSettingViewBaseEvent.DeviceSettingLockScreenTimeEvent event) {
        updateDeviceSettingPage(R.id.setting_lock_screen_time);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAutomaticShutDownEvent(DeviceSettingViewBaseEvent.DeviceSettingAutomaticShutDownEvent event) {
        updateDeviceSettingPage(R.id.setting_automatic_shutdown);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageSettingsEvent(DeviceSettingViewBaseEvent.DeviceSettingLanguageSettingsEvent event) {
        updateDeviceSettingPage(R.id.setting_language_settings);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInformationEvent(DeviceSettingViewBaseEvent.DeviceSettingDeviceInformationEvent event) {
        updateDeviceSettingPage(R.id.setting_device_information);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceResetInformationEvent(DeviceSettingViewBaseEvent.DeviceResetInformationEvent event) {
        ManagerActivityUtils.startResetDeviceActivity(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewDeviceStorageEvent(DeviceSettingViewBaseEvent.DeviceSettingViewDeviceStorageEvent event) {
        updateDeviceSettingPage(R.id.setting_device_storage_information);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckUpdateEvent(DeviceSettingViewBaseEvent.DeviceSettingCheckUpdateEvent event) {
        updateDeviceSettingPage(R.id.setting_system_update);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenSystemSettingEvent(DeviceSettingViewBaseEvent.OpenSystemSettingEvent event) {
        openSystemSetting();
    }

    private void openSystemSetting() {
        if (TimeUtils.getCurrentTimeInLong() > lastPressTime + Constants.RESET_PRESS_TIMEOUT) {
            lastPressTime = TimeUtils.getCurrentTimeInLong();
            resetPressCount = 0;
        }
        resetPressCount++;
        if (resetPressCount >= Constants.SYSTEM_SETTING_PRESS_COUNT) {
            resetPressCount = 0;
            lastPressTime = 0;
            SystemUtils.startSystemSettingActivity(getActivity());
        }
    }

    private void updateDeviceSettingPage(final int pageID) {
        settingPageView.setVisibility(pageID == R.id.setting_page ? View.VISIBLE : View.GONE);
        lockScreenTimeView.setVisibility(pageID == R.id.setting_lock_screen_time ? View.VISIBLE : View.GONE);
        automaticShutdownView.setVisibility(pageID == R.id.setting_automatic_shutdown ? View.VISIBLE : View.GONE);
        languageSettingsView.setVisibility(pageID == R.id.setting_language_settings ? View.VISIBLE : View.GONE);
        deviceInformationView.setVisibility(pageID == R.id.setting_device_information ? View.VISIBLE : View.GONE);
        if (pageID == R.id.setting_device_storage_information) {
            updateDeviceInformation();
        }

        if (deviceStorageInformationView != null) {
            deviceStorageInformationView.setVisibility(pageID == R.id.setting_device_storage_information ? View.VISIBLE : View.GONE);
        }
        systemUpdateView.setVisibility(pageID == R.id.setting_system_update ? View.VISIBLE : View.GONE);

        if (pageID == R.id.setting_page) {
            deviceSettingAdapter.setValues(deviceSettingPresenter.getDeviceSettingValue(getActivity()));
            deviceSettingAdapter.notifyDataSetChanged();
        }
        currentPageID = pageID;
    }

    private void updateDeviceInformation() {
        deviceSettingPresenter.updateDeviceInformation(getActivity());
        if (deviceSettingDeviceStorageInformationAdapter != null) {
            deviceSettingDeviceStorageInformationAdapter.setDeviceStorageInformations(deviceSettingPresenter.getDeviceStorageInformationList());
            deviceSettingDeviceStorageInformationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        currentPageID = R.id.setting_page;
        deviceSettingPresenter = new DeviceSettingPresenter(this);
        deviceSettingPresenter.loadConfigData(getActivity());
        settingBinding = (DeviceSettingFragmentBinding) binding;
        initSettingListPageView(settingBinding.getRoot());
        initLockScreenTimeView(settingBinding.getRoot());
        initAutomaticShutdownView(settingBinding.getRoot());
        initLanguageSettingsView(settingBinding.getRoot());
        initDeviceInformationView(settingBinding.getRoot());
        initSystemUpdateView(settingBinding.getRoot());
    }

    @Override
    protected int getRootView() {
        return R.layout.device_setting_fragment;
    }


    @Override
    public void setLatestVersionMessage(DeviceVersionEntity entity) {
        if (entity == null || StringUtils.isNullOrEmpty(entity.Ver) || entity.Ver.equals(ApkUtils.getSoftwareVersionName())) {
            CommonNotices.show(getString(R.string.without_new_version));
            return;
        }
    }

    private void deleteUpdateFile(String path, String apkName) {
        File file = new File(path, apkName);
        FileUtils.deleteFile(file.getAbsolutePath());
    }

    private boolean testUpdate() {
        File file = new File(Environment.getExternalStorageDirectory() + "/.version/version");
        return file.exists();
    }

    private String testUpdateUrl() {
        FileInputStream fis = null;
        BufferedReader br = null;
        String updateUrl = "";
        try {
            fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/.version/version");
            br = new BufferedReader(new InputStreamReader(fis));
            updateUrl = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updateUrl;
    }

    @Override
    public boolean onKeyBack() {
        switch (currentPageID) {
            case R.id.setting_lock_screen_time:
            case R.id.setting_automatic_shutdown:
            case R.id.setting_language_settings:
            case R.id.setting_device_information:
                updateDeviceSettingPage(R.id.setting_page);
                return true;
            case R.id.setting_device_storage_information:
                updateDeviceSettingPage(R.id.setting_device_information);
                return true;
            case R.id.setting_system_update:
                updateDeviceSettingPage(R.id.setting_device_information);
                return true;
        }
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public boolean onKeyPageUp() {
        return false;
    }

    @Override
    public boolean onKeyPageDown() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_screen_time_save:
                onLockScreenTimeSaveClick(v);
                break;
            case R.id.automatic_shutdown_save:
                onAutomaticShutdownSaveClick(v);
                break;
            case R.id.system_update_check_update:
                onCheckSystemUpdateClick(v);
                break;
            case R.id.language_settings_save:
                onLanguageSettingSaveClick(v);
                break;
            case R.id.device_information_title:
                onDeviceInformationTitleClick();
                break;
        }
    }

    public void onDeviceInformationTitleClick() {
        if (TimeUtils.getCurrentTimeInLong() > deviceInformationTitleLastPressTime + Constants.RESET_PRESS_TIMEOUT) {
            deviceInformationTitleLastPressTime = TimeUtils.getCurrentTimeInLong();
            deviceInformationTitleResetPressCount = 0;
        }
        deviceInformationTitleResetPressCount++;
        deviceInformationTitleResetPressCount = 0;
        deviceInformationTitleLastPressTime = 0;
    }

    public void onLockScreenTimeSaveClick(View v) {
        deviceSettingPresenter.setCurrentScreenTimeout(getActivity(), deviceSettingLockScreenTimeAdapter.getCurrentScreenTimeout());
        CommonNotices.show(getString(R.string.saved_successfully));
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void onAutomaticShutdownSaveClick(View v) {
        deviceSettingPresenter.setCurrentTimeoutValue(getActivity(), deviceSettingAutomaticShutdownAdapter.getCurrentTimeValue());
        CommonNotices.show(getString(R.string.saved_successfully));
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void onCheckSystemUpdateClick(View v) {
        final String path = LocalPackageUpdate.checkLocalSystemPakage();
        if (StringUtils.isNotBlank(path)) {
            final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.local_update));
            builder.setPositiveButton(getString(R.string.local_update), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setNegativeButton(getString(R.string.online_update), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onlineUpdate();
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            onlineUpdate();
        }
    }

    public void onlineUpdate() {
        CommonNotices.show(getString(R.string.device_setting_check_system_update));
        if (!NetworkUtil.isNetworkConnected(SunApplication.getInstance())) {
            NetworkUtil.toggleWiFi(SunApplication.getInstance(), true);
            return;
        }
        ApkUtils.firmwareCloudCheck(true);
    }

    public void onLanguageSettingSaveClick(View v) {
        Locale locale = deviceSettingLanguageSettingsAdapter.getLocale();
        if (locale != null) {
            if (SystemLanguage.updateLocale(locale)) {
                CommonNotices.show(getString(R.string.saved_successfully));
            } else {
                CommonNotices.show(getString(R.string.save_failed));
            }
        }
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void setLanguageCurrentName(final String languageName) {
        languageSettingsTitle.setText(getString(R.string.device_setting_language_settings_title) + "  (  " + languageName + "  )");
    }
}
