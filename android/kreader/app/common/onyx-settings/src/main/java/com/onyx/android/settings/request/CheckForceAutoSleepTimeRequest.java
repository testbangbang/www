package com.onyx.android.settings.request;

import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.libsetting.util.PowerUtil;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.settings.device.DeviceConfig;

/**
 * Created by suicheng on 2018/2/5.
 */
public class CheckForceAutoSleepTimeRequest extends BaseDataRequest {

    @Override
    public void execute(DataManager dataManager) throws Exception {
        int time = DeviceConfig.sharedInstance(getContext()).getForceAutoSleepTime();
        if (time < 0) {
            return;
        }
        int preferenceTime = SettingsPreferenceManager.getForceAutoSleepTime(getContext());
        if (preferenceTime != time) {
            SettingsPreferenceManager.setForceAutoSleepTime(getContext(), time);
            PowerUtil.setCurrentTimeoutValue(getContext(),
                    PowerSettingTimeoutCategory.SCREEN_TIMEOUT, time);
        }
    }
}
