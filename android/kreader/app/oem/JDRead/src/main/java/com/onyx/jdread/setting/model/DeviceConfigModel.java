package com.onyx.jdread.setting.model;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.setting.event.DeviceConfigEvent;
import com.onyx.jdread.setting.event.DeviceInformationEvent;
import com.onyx.jdread.setting.event.PasswordEvent;
import com.onyx.jdread.setting.event.ReadToolEvent;
import com.onyx.jdread.setting.event.ScreenSaverEvent;
import com.onyx.jdread.setting.event.SystemUpdateEvent;
import com.onyx.jdread.setting.utils.UpdateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/22.
 */

public class DeviceConfigModel {
    private List<DeviceConfigData> deviceConfigDataList = new ArrayList<>();
    private Map<String, DeviceConfigEvent> configEvents = new HashMap<String, DeviceConfigEvent>(){
        {
            put(JDReadApplication.getInstance().getResources().getString(R.string.screen_saver), new ScreenSaverEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.password), new PasswordEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.read_tool), new ReadToolEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.device_information), new DeviceInformationEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.system_update), new SystemUpdateEvent());
        }
    };

    public List<DeviceConfigData> getDeviceConfigDataList() {
        return deviceConfigDataList;
    }

    public Map<String, DeviceConfigEvent> getConfigEvents() {
        return configEvents;
    }

    public void loadDeviceConfig() {
        if (deviceConfigDataList != null && deviceConfigDataList.size() > 0) {
            deviceConfigDataList.clear();
        }
        String[] configs = JDReadApplication.getInstance().getResources().getStringArray(R.array.device_configs);
        for (int i = 0; i < configs.length; i++) {
            DeviceConfigData deviceConfigData = new DeviceConfigData();
            deviceConfigData.setConfigName(configs[i]);
            deviceConfigData.setHasToggle(i == 0);
            deviceConfigDataList.add(deviceConfigData);
        }

        String localPath = UpdateUtil.checkLocalPackage();
        if (FileUtils.fileExist(localPath) || FileUtils.fileExist(UpdateUtil.getApkUpdateFile())) {
            DeviceConfigData deviceConfigData = deviceConfigDataList.get(deviceConfigDataList.size() - 1);
            deviceConfigData.setUpdateRecord("1");
        }
    }
}
