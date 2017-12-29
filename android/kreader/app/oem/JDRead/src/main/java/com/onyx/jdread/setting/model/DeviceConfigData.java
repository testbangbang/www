package com.onyx.jdread.setting.model;

/**
 * Created by li on 2017/12/22.
 */

public class DeviceConfigData {
    private String configName;
    private String updateRecord;
    private boolean hasToggle;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getUpdateRecord() {
        return updateRecord;
    }

    public void setUpdateRecord(String updateRecord) {
        this.updateRecord = updateRecord;
    }

    public boolean isHasToggle() {
        return hasToggle;
    }

    public void setHasToggle(boolean hasToggle) {
        this.hasToggle = hasToggle;
    }
}
