package com.onyx.jdread.setting.model;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/20.
 */

public class SettingBundle {
    private EventBus eventBus = EventBus.getDefault();
    private static SettingBundle bundle;
    private SettingDataModel settingDataModel;
    private SettingTitleModel titleModel;

    public static SettingBundle getInstance() {
        if (bundle == null) {
            bundle = new SettingBundle();
        }
        return bundle;
    }

    public EventBus getEventBus() {
        return eventBus;
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
}
