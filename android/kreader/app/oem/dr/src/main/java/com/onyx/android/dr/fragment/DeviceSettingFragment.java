package com.onyx.android.dr.fragment;

import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DeviceSettingAdapter;
import com.onyx.android.dr.adapter.DeviceSettingAutomaticShutdownAdapter;
import com.onyx.android.dr.adapter.DeviceSettingDeviceInformationAdapter;
import com.onyx.android.dr.adapter.DeviceSettingDeviceStorageInformationAdapter;
import com.onyx.android.dr.adapter.DeviceSettingLanguageSettingsAdapter;
import com.onyx.android.dr.adapter.DeviceSettingLockScreenTimeAdapter;
import com.onyx.android.dr.adapter.DeviceSettingPageRefreshesAdapter;
import com.onyx.android.dr.adapter.DeviceSettingSystemUpdateAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.CustomEditDialog;
import com.onyx.android.dr.event.DeviceSettingViewBaseEvent;
import com.onyx.android.dr.interfaces.DeviceSettingView;
import com.onyx.android.dr.presenter.DeviceSettingPresenter;
import com.onyx.android.dr.util.ApkUtils;
import com.onyx.android.dr.util.SystemLanguage;
import com.onyx.android.dr.util.SystemUtils;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

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
public class DeviceSettingFragment extends BaseFragment implements DeviceSettingView {
    private static final String TAG = DeviceSettingFragment.class.getSimpleName();
    private LinearLayout settingPageView;
    private PageRecyclerView settingRecyclerView;
    private DeviceSettingPresenter deviceSettingPresenter;
    private DeviceSettingAdapter deviceSettingAdapter;

    private RelativeLayout pagerRefreshView;
    private PageRecyclerView pageRefreshRecyclerView;
    private DeviceSettingPageRefreshesAdapter deviceSettingPageRefreshesAdapter;
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
    private Button systemUpdateCheck;
    private Switch pageRefreshSwitch;

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
    protected void initListener() {
        deviceSettingPageRefreshesAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                onPageRefreshSaveClick();
            }
        });

        deviceSettingLockScreenTimeAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                onLockScreenTimeSaveClick();
            }
        });

        deviceSettingAutomaticShutdownAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                onAutomaticShutdownSaveClick();
            }
        });

        deviceSettingSystemUpdateAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                String tag = (String) view.getTag();
                if (getString(R.string.system_update).equals(tag)) {
                    onCheckSystemUpdateClick();
                } else if (getString(R.string.bookstore_update).equals(tag)) {
                    onCheckAPKUpdateClick();
                }
            }
        });

        deviceSettingLanguageSettingsAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                String[] data = DRApplication.getInstance().getResources().getStringArray(R.array.device_setting_language);
                showConfirmDialog(data[(int) position], (int) position);
            }
        });

        systemUpdateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApkUtils.firmwareCloudCheck(true);
            }
        });

        pageRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pageRefreshRecyclerView.setEnabled(!isChecked);
                deviceSettingPageRefreshesAdapter.setA2Checked(isChecked);
            }
        });
    }

    private void onCheckAPKUpdateClick() {
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.device_setting_check_system_update));
        if (!NetworkUtil.isWiFiConnected(getActivity())) {
            Device.currentDevice().enableWifiDetect(DRApplication.getInstance());
            NetworkUtil.enableWiFi(DRApplication.getInstance(), true);
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.please_connect_to_the_network_first));
            return;
        }
        ApkUtils.updateApk(true);
    }

    private void showConfirmDialog(final String language, final int position) {
        CustomEditDialog.Builder builder = new CustomEditDialog.Builder(getActivity());
        TextView textView = new TextView(getActivity());
        textView.setText(getString(R.string.change_system_language) + language + "?");
        textView.setWidth(getResources().getInteger(R.integer.delete_dialog_message_width));
        builder.setTitle(getString(R.string.prompt)).setContentView(textView).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deviceSettingLanguageSettingsAdapter.setConfirm(false, position);
                dialog.dismiss();
            }
        }).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deviceSettingLanguageSettingsAdapter.setConfirm(true, position);
                onLanguageSettingSaveClick();
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    protected void initView(View rootView) {
        currentPageID = R.id.setting_page;
        deviceSettingPresenter = new DeviceSettingPresenter(this);
        deviceSettingPresenter.loadConfigData(DRApplication.getInstance());
        initSettingListPageView(rootView);
        initPageRefreshView(rootView);
        initLockScreenTimeView(rootView);
        initAutomaticShutdownView(rootView);
        initLanguageSettingsView(rootView);
        initDeviceInformationView(rootView);
        initDeviceStorageInformationView(rootView);
        initSystemUpdateView(rootView);
    }

    private void initSettingListPageView(View rootView) {
        settingPageView = (LinearLayout) rootView.findViewById(R.id.setting_page);
        deviceSettingAdapter = new DeviceSettingAdapter(DRApplication.getInstance());
        deviceSettingAdapter.setTitles(deviceSettingPresenter.getDeviceSettingTitle());
        deviceSettingAdapter.setValues(deviceSettingPresenter.getDeviceSettingValue(DRApplication.getInstance()));
        settingRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.setting_recycler_view);
        settingRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        settingRecyclerView.addItemDecoration(dividerItemDecoration);
        settingRecyclerView.setAdapter(deviceSettingAdapter);
    }

    private void initPageRefreshView(View rootView) {
        deviceSettingPageRefreshesAdapter = new DeviceSettingPageRefreshesAdapter(DRApplication.getInstance());
        deviceSettingPageRefreshesAdapter.setRefreshRate(deviceSettingPresenter.getDeviceSettingPageRefreshes());
        pagerRefreshView = (RelativeLayout) rootView.findViewById(R.id.setting_page_refresh);

        pageRefreshRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.page_refresh_recycler_view);
        pageRefreshSwitch = (Switch) rootView.findViewById(R.id.page_refreshes_switch);
        pageRefreshRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        pageRefreshRecyclerView.addItemDecoration(dividerItemDecoration);
        pageRefreshRecyclerView.setAdapter(deviceSettingPageRefreshesAdapter);
    }

    private void initLockScreenTimeView(View rootView) {
        deviceSettingLockScreenTimeAdapter = new DeviceSettingLockScreenTimeAdapter(DRApplication.getInstance());
        deviceSettingLockScreenTimeAdapter.setTimes(deviceSettingPresenter.getDeviceSettingLockScreenTime(),
                deviceSettingPresenter.getDeviceSettingLockScreenTimeValue(),
                deviceSettingPresenter.getCurrentScreenTimeout());
        lockScreenTimeView = (RelativeLayout) rootView.findViewById(R.id.setting_lock_screen_time);

        lockScreenTimeRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.lock_screen_time_recycler_view);
        lockScreenTimeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        lockScreenTimeRecyclerView.addItemDecoration(dividerItemDecoration);
        lockScreenTimeRecyclerView.setAdapter(deviceSettingLockScreenTimeAdapter);
    }

    private void initAutomaticShutdownView(View rootView) {
        deviceSettingAutomaticShutdownAdapter = new DeviceSettingAutomaticShutdownAdapter(DRApplication.getInstance());
        deviceSettingAutomaticShutdownAdapter.setAutomaticShutdownTimes(deviceSettingPresenter.getDeviceSettingAutomaticShutdownTime(),
                deviceSettingPresenter.getDeviceSettingAutomaticShutdownTimeValue(),
                deviceSettingPresenter.getDeviceSettingAutomaticShutdownTimeExplain(),
                deviceSettingPresenter.getCurrentTimeoutValue());
        automaticShutdownView = (RelativeLayout) rootView.findViewById(R.id.setting_automatic_shutdown);

        automaticShutdownRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.automatic_shutdown_recycler_view);
        automaticShutdownRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        automaticShutdownRecyclerView.addItemDecoration(dividerItemDecoration);
        automaticShutdownRecyclerView.setAdapter(deviceSettingAutomaticShutdownAdapter);
    }

    private void initLanguageSettingsView(View rootView) {
        deviceSettingLanguageSettingsAdapter = new DeviceSettingLanguageSettingsAdapter(DRApplication.getInstance());
        deviceSettingLanguageSettingsAdapter.setLanguages(deviceSettingPresenter.getLocaleLanguageInfoList());
        languageSettingsView = (RelativeLayout) rootView.findViewById(R.id.setting_language_settings);

        languageSettingsRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.language_settings_recycler_view);
        languageSettingsRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        languageSettingsRecyclerView.addItemDecoration(dividerItemDecoration);
        languageSettingsRecyclerView.setAdapter(deviceSettingLanguageSettingsAdapter);

        languageSettingsTitle = (TextView) rootView.findViewById(R.id.language_settings_title);
        setLanguageCurrentName(deviceSettingPresenter.getLocaleLanguageInfoList().currentLanguage);
    }

    private void initDeviceInformationView(View rootView) {
        deviceSettingDeviceInformationAdapter = new DeviceSettingDeviceInformationAdapter(DRApplication.getInstance());
        deviceSettingDeviceInformationAdapter.setDeviceInformation(deviceSettingPresenter.getDeviceSettingDeviceInformation());
        deviceInformationView = (LinearLayout) rootView.findViewById(R.id.setting_device_information);

        deviceInformationRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.device_information_recycler_view);
        deviceInformationRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        deviceInformationRecyclerView.addItemDecoration(dividerItemDecoration);
        deviceInformationRecyclerView.setAdapter(deviceSettingDeviceInformationAdapter);
    }

    private void initDeviceStorageInformationView(View rootView) {
        deviceSettingDeviceStorageInformationAdapter = new DeviceSettingDeviceStorageInformationAdapter(DRApplication.getInstance());
        deviceSettingDeviceStorageInformationAdapter.setDeviceStorageInformations(deviceSettingPresenter.getDeviceStorageInformationList());
        deviceStorageInformationView = (LinearLayout) rootView.findViewById(R.id.setting_device_storage_information);

        deviceStorageInformationRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.device_storage_information_recycler_view);
        deviceStorageInformationRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        deviceStorageInformationRecyclerView.addItemDecoration(dividerItemDecoration);
        deviceStorageInformationRecyclerView.setAdapter(deviceSettingDeviceStorageInformationAdapter);
    }

    private void initSystemUpdateView(View rootView) {
        deviceSettingSystemUpdateAdapter = new DeviceSettingSystemUpdateAdapter(DRApplication.getInstance());
        deviceSettingSystemUpdateAdapter.setSystemVersionInformationList(deviceSettingPresenter.getSystemVersionInformationList());
        systemUpdateView = (LinearLayout) rootView.findViewById(R.id.setting_system_update);

        systemUpdateCheck = (Button) rootView.findViewById(R.id.system_update_check);
        systemUpdateRecyclerView = (PageRecyclerView) rootView.findViewById(R.id.system_update_recycler_view);
        systemUpdateRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        systemUpdateRecyclerView.addItemDecoration(dividerItemDecoration);
        systemUpdateRecyclerView.setAdapter(deviceSettingSystemUpdateAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageRefreshEvent(DeviceSettingViewBaseEvent.DeviceSettingPageRefreshEvent event) {
        updateDeviceSettingPage(R.id.setting_page_refresh);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceSettingUserInfoEvent(DeviceSettingViewBaseEvent.DeviceSettingUserInfoEvent event) {
        if (DRApplication.getInstance().isLoginSuccess()) {
            ActivityManager.startUserInfoActivity(DRApplication.getInstance().getBaseContext());
        } else {
            ActivityManager.startLoginActivity(DRApplication.getInstance().getBaseContext());
        }
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
        ActivityManager.startResetDeviceActivity(DRApplication.getInstance());
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
    public void onViewSystemVersionHistoryEvent(DeviceSettingViewBaseEvent.DeviceSettingViewSystemVersionHistoryEvent event) {
        ActivityManager.startSystemUpdateHistoryActivity(DRApplication.getInstance());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenSystemSettingEvent(DeviceSettingViewBaseEvent.OpenSystemSettingEvent event) {
        openSystemSetting();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceSettingWifiSettingEvent(DeviceSettingViewBaseEvent.DeviceSettingWifiSettingEvent event) {
        ActivityManager.startWifiActivity(DRApplication.getInstance());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceSettingTimeSettingEvent(DeviceSettingViewBaseEvent.DeviceSettingTimeSettingEvent event) {
        ActivityManager.startDateTimeSettingsActivity(DRApplication.getInstance());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceSettingTTSSettingEvent(DeviceSettingViewBaseEvent.DeviceSettingTTSSettingEvent event) {
        ActivityManager.startTTSSettingsActivity(DRApplication.getInstance());
    }


    private void openSystemSetting() {
        if (TimeUtils.getCurrentTimeInLong() > lastPressTime + Constants.RESET_PRESS_TIMEOUT) {
            lastPressTime = TimeUtils.getCurrentTimeInLong();
            resetPressCount = Constants.VALUE_ZERO;
        }
        resetPressCount++;
        if (resetPressCount >= Constants.SYSTEM_SETTING_PRESS_COUNT) {
            resetPressCount = Constants.VALUE_ZERO;
            lastPressTime = Constants.VALUE_ZERO;
            SystemUtils.startSystemSettingActivity(getActivity());
        }
    }

    private void updateDeviceSettingPage(final int pageID) {
        pagerRefreshView.setVisibility(pageID == R.id.setting_page_refresh ? View.VISIBLE : View.GONE);
        settingPageView.setVisibility(pageID == R.id.setting_page ? View.VISIBLE : View.GONE);
        lockScreenTimeView.setVisibility(pageID == R.id.setting_lock_screen_time ? View.VISIBLE : View.GONE);
        automaticShutdownView.setVisibility(pageID == R.id.setting_automatic_shutdown ? View.VISIBLE : View.GONE);
        languageSettingsView.setVisibility(pageID == R.id.setting_language_settings ? View.VISIBLE : View.GONE);
        deviceInformationView.setVisibility(pageID == R.id.setting_device_information ? View.VISIBLE : View.GONE);
        if (pageID == R.id.setting_device_storage_information) {
            updateDeviceInformation();
        }
        deviceStorageInformationView.setVisibility(pageID == R.id.setting_device_storage_information ? View.VISIBLE : View.GONE);
        systemUpdateView.setVisibility(pageID == R.id.setting_system_update ? View.VISIBLE : View.GONE);

        if (pageID == R.id.setting_page) {
            deviceSettingAdapter.setValues(deviceSettingPresenter.getDeviceSettingValue(DRApplication.getInstance()));
            deviceSettingAdapter.notifyDataSetChanged();
        }
        currentPageID = pageID;
    }

    private void updateDeviceInformation() {
        deviceSettingPresenter.updateDeviceInformation(DRApplication.getInstance());
        if (deviceSettingDeviceStorageInformationAdapter != null) {
            deviceSettingDeviceStorageInformationAdapter.setDeviceStorageInformations(deviceSettingPresenter.getDeviceStorageInformationList());
            deviceSettingDeviceStorageInformationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected int getRootView() {
        return R.layout.device_setting_fragment;
    }


    @Override
    public void loadConfigDataFinish() {
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
            case R.id.setting_page_refresh:
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
        return false;
    }

    @Override
    public boolean onKeyPageUp() {
        return false;
    }

    @Override
    public boolean onKeyPageDown() {
        return false;
    }

    public void onPageRefreshSaveClick() {
        deviceSettingPresenter.setCurrentPageRefreshTime(DRApplication.getInstance(), deviceSettingPageRefreshesAdapter.getSelectItem());
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.saved_successfully));
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void onLockScreenTimeSaveClick() {
        deviceSettingPresenter.setCurrentScreenTimeout(DRApplication.getInstance(), deviceSettingLockScreenTimeAdapter.getCurrentScreenTimeout());
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.saved_successfully));
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void onAutomaticShutdownSaveClick() {
        deviceSettingPresenter.setCurrentTimeoutValue(DRApplication.getInstance(), deviceSettingAutomaticShutdownAdapter.getCurrentTimeValue());
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.saved_successfully));
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void onCheckSystemUpdateClick() {
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.device_setting_check_system_update));
        if (!NetworkUtil.isWiFiConnected(getActivity())) {
            Device.currentDevice().enableWifiDetect(DRApplication.getInstance());
            NetworkUtil.enableWiFi(DRApplication.getInstance(), true);
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.please_connect_to_the_network_first));
        }
        ApkUtils.firmwareCloudCheck(true);
    }

    public void onLanguageSettingSaveClick() {
        Locale locale = deviceSettingLanguageSettingsAdapter.getLocale();
        if (locale != null) {
            if (SystemLanguage.updateLocale(locale)) {
                CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.saved_successfully));
            } else {
                CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.save_failed));
            }
        }
        updateDeviceSettingPage(R.id.setting_page);
    }

    public void setLanguageCurrentName(final String languageName) {
        languageSettingsTitle.setText(getString(R.string.device_setting_language_settings_title) + "  (  " + languageName + "  )");
    }
}
