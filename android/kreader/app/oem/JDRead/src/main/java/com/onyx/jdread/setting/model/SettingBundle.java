package com.onyx.jdread.setting.model;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/20.
 */

public class SettingBundle {
    private EventBus eventBus = EventBus.getDefault();
    private CloudManager cloudManager = new CloudManager();
    private DataManager dataManager = new DataManager();
    private static SettingBundle bundle;
    private SettingDataModel settingDataModel;
    private SettingTitleModel titleModel;
    private SettingRefreshModel settingRefreshModel;
    private SettingLockScreenModel settingLockScreenModel;
    private DeviceConfigModel deviceConfigModel;
    private boolean firmwareValid;
    private ApplicationUpdate applicationUpdate;
    private SettingUpdateModel settingUpdateModel;
    private Firmware resultFirmware;

    public static SettingBundle getInstance() {
        if (bundle == null) {
            bundle = new SettingBundle();
        }
        return bundle;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public void setCloudManager(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    public SettingDataModel getSettingDataModel() {
        if (settingDataModel == null) {
            settingDataModel = new SettingDataModel();
            settingDataModel.loadSettingData();
        }
        return settingDataModel;
    }

    public SettingTitleModel getTitleModel() {
        if (titleModel == null) {
            titleModel = new SettingTitleModel(eventBus);
        }
        return titleModel;
    }

    public SettingRefreshModel getSettingRefreshModel() {
        if (settingRefreshModel == null) {
            settingRefreshModel = new SettingRefreshModel();
        }
        return settingRefreshModel;
    }

    public SettingLockScreenModel getSettingLockScreenModel() {
        if (settingLockScreenModel == null) {
            settingLockScreenModel = new SettingLockScreenModel();
        }
        return settingLockScreenModel;
    }

    public DeviceConfigModel getDeviceConfigModel() {
        if (deviceConfigModel == null) {
            deviceConfigModel = new DeviceConfigModel();
        }
        deviceConfigModel.loadDeviceConfig();
        return deviceConfigModel;
    }

    public SettingUpdateModel getSettingUpdateModel() {
        if (settingUpdateModel == null) {
            settingUpdateModel = new SettingUpdateModel();
        }
        return settingUpdateModel;
    }

    public void setFirmwareValid(boolean firmwareValid) {
        this.firmwareValid = firmwareValid;
    }

    public boolean getFirmwareValid() {
        return firmwareValid;
    }

    public void setApplicationUpdate(ApplicationUpdate applicationUpdate) {
        this.applicationUpdate = applicationUpdate;
    }

    public ApplicationUpdate getApplicationUpdate() {
        return applicationUpdate;
    }

    public void setResultFirmware(Firmware resultFirmware) {
        this.resultFirmware = resultFirmware;
    }

    public Firmware getResultFirmware() {
        return resultFirmware;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
