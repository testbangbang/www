package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.libsetting.util.BatteryUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SystemUpdateBinding;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.PopCurrentChildViewEvent;
import com.onyx.jdread.main.event.TitleBarRightTitleEvent;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.action.CheckApkUpdateAction;
import com.onyx.jdread.setting.action.DownloadPackageAction;
import com.onyx.jdread.setting.action.OnlineCheckSystemUpdateAction;
import com.onyx.jdread.setting.action.SystemUpdateHistoryAction;
import com.onyx.jdread.setting.dialog.CheckUpdateLoadingDialog;
import com.onyx.jdread.setting.dialog.DialogMessage;
import com.onyx.jdread.setting.dialog.SystemUpdateDialog;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.DelayEvent;
import com.onyx.jdread.setting.event.ExecuteUpdateEvent;
import com.onyx.jdread.setting.model.DeviceConfigData;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingUpdateModel;
import com.onyx.jdread.setting.model.SystemUpdateData;
import com.onyx.jdread.setting.utils.UpdateUtil;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.BookInfoDialog;
import com.onyx.jdread.util.TimeUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/22.
 */

public class SystemUpdateFragment extends BaseFragment {
    private static final String TAG = SystemUpdateFragment.class.getSimpleName();
    private SystemUpdateBinding binding;
    private CheckUpdateLoadingDialog checkUpdateLoadingDialog;
    private SystemUpdateData systemUpdateData;
    private String downloadUrl;
    private String downloadPath;
    private String tag;
    private SettingUpdateModel settingUpdateModel;
    private CheckApkUpdateAction apkUpdateAction;
    private DownloadPackageAction downloadPackageAction;

    private BookInfoDialog updateHistoryDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (SystemUpdateBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_system_update, container, false);
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideAllDialog();
    }

    private void initListener() {
        binding.upgradeImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JDReadApplication.getInstance().getString(R.string.download_update_package).equals(systemUpdateData.getUpdateDes())) {
                    downloadUpdatePackage();
                } else if (JDReadApplication.getInstance().getString(R.string.upgrade_immediately).equals(systemUpdateData.getUpdateDes())) {
                    showDialog();
                } else {
                    checkSystemUpdate();
                }
            }
        });
    }

    private void downloadUpdatePackage() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        systemUpdateData.setShowProgress(true);
        downloadPackageAction = new DownloadPackageAction(downloadUrl, downloadPath, tag);
        downloadPackageAction.execute(callback);
    }

    private BaseCallback callback = new BaseCallback() {
        @Override
        public void start(BaseRequest request) {

        }

        @Override
        public void progress(BaseRequest request, ProgressInfo info) {
            systemUpdateData.setProgress((int) info.progress);
        }

        @Override
        public void done(BaseRequest request, Throwable e) {
            finishSystemUpdatePackageDownload(e == null);
        }
    };

    private void finishSystemUpdatePackageDownload(boolean success) {
        if (systemUpdateData == null || settingUpdateModel == null) {
            return;
        }
        systemUpdateData.setProgress(success ? 100 : 0);
        systemUpdateData.setShowProgress(false);
        systemUpdateData.setShowDownloaded(success);
        systemUpdateData.setUpdateDes(ResManager.getString(success ? R.string.upgrade_immediately : R.string.download_update_package));
        settingUpdateModel.setDownloaded(success);
    }

    private void initData() {
        TitleBarModel titleModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
        titleModel.title.set(ResManager.getString(R.string.system_update));
        titleModel.rightTitle.set(getString(R.string.view_history_version));
        titleModel.backEvent.set(new BackToDeviceConfigFragment());
        binding.systemUpdateSettingBar.setTitleModel(titleModel);

        settingUpdateModel = SettingBundle.getInstance().getSettingUpdateModel();
        systemUpdateData = settingUpdateModel.getSystemUpdateData();
        if (!isApkExist() && !isUpdateZipExist() && settingUpdateModel.isDownloaded()) {
            settingUpdateModel.setDownloaded(false);
        } else if (StringUtils.isNotBlank(systemUpdateData.getNoticeMessage())) {
            binding.setModel(settingUpdateModel);
            return;
        }
        List<DeviceConfigData> deviceConfigDataList = SettingBundle.getInstance().getDeviceConfigModel().getDeviceConfigDataList();
        if (StringUtils.isNotBlank(deviceConfigDataList.get(deviceConfigDataList.size() - 1).getUpdateRecord())) {
            checkSystemUpdate();
            systemUpdateData.setShowDownloaded(true);
            systemUpdateData.setUpdateDes(ResManager.getString(R.string.upgrade_immediately));
            systemUpdateData.setVersionTitle(ResManager.getString(R.string.updatable_version));
        } else {
            systemUpdateData.setShowDownloaded(false);
            systemUpdateData.setUpdateDes(ResManager.getString(R.string.check_update));
            systemUpdateData.setVersionTitle(ResManager.getString(R.string.current_version));
        }
        systemUpdateData.setVersion(settingUpdateModel.getDownloadVersion());
        systemUpdateData.setShowProgress(false);
        binding.setModel(settingUpdateModel);
    }

    private void checkSystemUpdate() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        final OnlineCheckSystemUpdateAction checkAction = new OnlineCheckSystemUpdateAction();
        checkAction.showLoadingDialog(SettingBundle.getInstance(), R.string.new_version_detection);
        checkAction.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (SettingBundle.getInstance().getFirmwareValid()) {
                    Firmware resultFirmware = SettingBundle.getInstance().getResultFirmware();
                    String changeLog = resultFirmware.getChangeLog();
                    if (StringUtils.isNullOrEmpty(changeLog)) {
                        changeLog = resultFirmware.buildDisplayId;
                    }
                    downloadUrl = resultFirmware.getUrl();
                    downloadPath = UpdateUtil.getUpdateZipFile().getAbsolutePath();
                    tag = UpdateUtil.SYSTEM_UPDATE_TAG;
                    systemUpdateData.setUpdateDes(isUpdateZipExist() ? ResManager.getString(R.string.upgrade_immediately) : ResManager.getString(R.string.download_update_package));
                    systemUpdateData.setVersionTitle(ResManager.getString(R.string.updatable_version));
                    String fingerprint = resultFirmware.fingerprint;
                    Log.i(TAG, "checkSystemUpdate: " + fingerprint);
                    String[] split = fingerprint.split("/");
                    String versionTime = getVersionTime(split);
                    Log.i(TAG, "system current version: " + settingUpdateModel.getDownloadVersion());
                    systemUpdateData.setVersion(UpdateUtil.VERSION_LAUNCHER + versionTime);
                    systemUpdateData.setNoticeMessage(changeLog);
                    checkAction.hideLoadingDialog(SettingBundle.getInstance());
                } else {
                    checkApkUpdate();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                checkAction.hideLoadingDialog(SettingBundle.getInstance());
                ToastUtil.showToast(R.string.network_or_server_error);
            }
        });
    }

    private String getVersionTime(String[] split) {
        String versionTime = "";
        if (split.length > 3) {
            String time = split[3];
            if (time.contains("_")) {
                time = time.substring(0, time.lastIndexOf("_"));
                time = time.substring(0, time.lastIndexOf("_"));
            }
            versionTime = TimeUtils.getFormatTime(time);
        }
        return versionTime;
    }

    private void checkApkUpdate() {
        apkUpdateAction = new CheckApkUpdateAction();
        apkUpdateAction.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ApplicationUpdate applicationUpdate = SettingBundle.getInstance().getApplicationUpdate();
                if (applicationUpdate != null) {
                    Map<String, List<String>> changeLogs = applicationUpdate.changeLogs;
                    String[] downloadUrlList = applicationUpdate.downloadUrlList;
                    String language = getResources().getConfiguration().locale.toString();
                    String message = "";
                    if (changeLogs != null && changeLogs.size() > 0) {
                        List<String> messageList = changeLogs.get(language);
                        for (int i = 0; i < messageList.size(); i++) {
                            message += messageList.get(i);
                            message += "\n";
                        }
                    }
                    if (downloadUrlList == null || downloadUrlList.length <= 0) {
                        apkUpdateAction.showLoadingDialog(SettingBundle.getInstance(), R.string.already_latest_version);
                        return;
                    }
                    downloadUrl = downloadUrlList[0];
                    downloadPath = UpdateUtil.getApkUpdateFile();
                    tag = UpdateUtil.APK_UPDATE_TAG;
                    systemUpdateData.setUpdateDes(isApkExist() ? ResManager.getString(R.string.upgrade_immediately) : ResManager.getString(R.string.download_update_package));
                    systemUpdateData.setVersionTitle(ResManager.getString(R.string.updatable_version));
                    Log.i(TAG, "checkApkUpdate: " + applicationUpdate.versionName);
                    String versionTime = applicationUpdate.versionName.substring(applicationUpdate.versionName.lastIndexOf("-") + 1, applicationUpdate.versionName.length());
                    Log.i(TAG, "apk current version: " + settingUpdateModel.getDownloadVersion());
                    systemUpdateData.setVersion(UpdateUtil.VERSION_LAUNCHER + versionTime);
                    systemUpdateData.setNoticeMessage(message);
                    apkUpdateAction.hideLoadingDialog(SettingBundle.getInstance());
                } else {
                    apkUpdateAction.showLoadingDialog(SettingBundle.getInstance(), R.string.already_latest_version);
                    DelayEvent delayEvent = new DelayEvent(2000);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                apkUpdateAction.hideLoadingDialog(SettingBundle.getInstance());
                ToastUtil.showToast(R.string.network_or_server_error);
            }
        });
    }

    private void showDialog() {
        SystemUpdateDialog dialog = new SystemUpdateDialog();
        dialog.setEventBus(SettingBundle.getInstance().getEventBus());
        dialog.show(getActivity().getFragmentManager(), "");
    }

    private boolean isApkExist() {
        File apkUpdateFile = new File(UpdateUtil.getApkUpdateFile());
        return apkUpdateFile.exists();
    }

    private boolean isUpdateZipExist() {
        File updateZipFile = UpdateUtil.getUpdateZipFile();
        return updateZipFile.exists();
    }

    private boolean checkBatteryForSystemUpgrade() {
        int minLimit = ResManager.getInteger(R.integer.system_update_min_battery_limit);
        if (!BatteryUtil.isPowerEnoughOrCharging(getContext().getApplicationContext(), minLimit)) {
            final DialogMessage dialog = new DialogMessage(getContext());
            dialog.setMessage(String.format(ResManager.getString(R.string.power_too_low_to_upgrade), minLimit));
            dialog.setPositiveAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExecuteUpdateEvent(ExecuteUpdateEvent event) {
        if (isApkExist()) {
            UpdateUtil.startUpdateApkActivity(JDReadApplication.getInstance(), downloadPath);
            return;
        }
        if (!checkBatteryForSystemUpgrade()) {
            return;
        }
        UpdateUtil.startSystemUpgradeActivity(getContext().getApplicationContext(), UpdateUtil.getUpdateZipFile().getAbsolutePath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelayEvent(DelayEvent event) {
        apkUpdateAction.hideLoadingDialog(SettingBundle.getInstance());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (checkUpdateLoadingDialog == null) {
            checkUpdateLoadingDialog = new CheckUpdateLoadingDialog();
        }
        if (!checkUpdateLoadingDialog.isVisible()) {
            checkUpdateLoadingDialog.setTips(event.getMessage());
            checkUpdateLoadingDialog.show(getActivity().getFragmentManager(), "");
        } else {
            checkUpdateLoadingDialog.setTips(event.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideAllDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToDeviceConfigFragment(BackToDeviceConfigFragment event) {
        if (!systemUpdateData.getShowDownloaded()) {
            systemUpdateData.setNoticeMessage("");
        }

        viewEventCallBack.viewBack();
    }

    @Subscribe
    public void onTitleBarRightTitleEvent(TitleBarRightTitleEvent event) {
        loadSystemUpdateHistory();
    }

    private void loadSystemUpdateHistory() {
        if (ViewHelper.dialogIsShowing(getLoadingDialog())) {
            return;
        }
        final SystemUpdateHistoryAction historyAction = new SystemUpdateHistoryAction();
        historyAction.execute(SettingBundle.getInstance(), new RxCallback() {

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable throwable) {
                hideLoadingDialog();
                showUpdateHistoryDialog(historyAction.getContent(), R.string.network_or_server_error);
            }

            @Override
            public void onComplete() {
                hideLoadingDialog();
                showUpdateHistoryDialog(historyAction.getContent(), R.string.update_history_empty);
            }
        });
        showLoadingDialog(ResManager.getString(R.string.loading));
    }

    private void showUpdateHistoryDialog(String content, int emptyTipId) {
        if (ViewHelper.dialogIsShowing(updateHistoryDialog)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            ToastUtil.showToast(emptyTipId);
            return;
        }
        updateHistoryDialog = new BookInfoDialog(JDReadApplication.getInstance());
        updateHistoryDialog.setTitle(ResManager.getString(R.string.view_history_version));
        updateHistoryDialog.setContent(content);
        updateHistoryDialog.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.dismissDialog(updateHistoryDialog);
                updateHistoryDialog = null;
            }
        });
        updateHistoryDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopCurrentChildViewEvent(PopCurrentChildViewEvent event) {
        if (!systemUpdateData.getShowDownloaded()) {
            systemUpdateData.setNoticeMessage("");
        }
    }

    private void hideAllDialog() {
        hideCheckUpdateLoadingDialog();
        ViewHelper.dismissDialog(updateHistoryDialog);
    }

    private void hideCheckUpdateLoadingDialog() {
        if (checkUpdateLoadingDialog != null) {
            checkUpdateLoadingDialog.dismiss();
        }
    }

    public void keepDownload() {
        if (downloadPackageAction != null && downloadPackageAction.getTask(tag) != null) {
            downloadPackageAction.execute(callback);
        }
    }
}
