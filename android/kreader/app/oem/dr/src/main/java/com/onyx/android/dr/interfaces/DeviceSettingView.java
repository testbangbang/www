package com.onyx.android.dr.interfaces;


import com.onyx.android.dr.data.database.DeviceVersionEntity;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public interface DeviceSettingView {
    void loadConfigDataFinish();

    void setLatestVersionMessage(DeviceVersionEntity entity);
}
