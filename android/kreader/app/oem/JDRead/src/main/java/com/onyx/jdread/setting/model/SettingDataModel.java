package com.onyx.jdread.setting.model;

import android.content.res.TypedArray;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.os.Build;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.setting.event.FeedbackEvent;
import com.onyx.jdread.setting.event.IntensityEvent;
import com.onyx.jdread.setting.event.LaboratoryEvent;
import com.onyx.jdread.setting.event.RefreshEvent;
import com.onyx.jdread.setting.event.ScreenEvent;
import com.onyx.jdread.setting.event.ToDeviceConfigEvent;
import com.onyx.jdread.setting.event.WireLessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/20.
 */

public class SettingDataModel extends BaseObservable {
    private List<SettingItemData> itemData = new ArrayList<>();
    public final ObservableField<String> deviceModel = new ObservableField<>();
    public final ObservableField<String> deviceVersion = new ObservableField<>();
    public final ObservableField<String> deviceSerial = new ObservableField<>();
    private Map<String, Object> itemEvent = new HashMap<String, Object>(){
        {
            put(JDReadApplication.getInstance().getResources().getString(R.string.wireless_network), new WireLessEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.intensity_control), new IntensityEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.page_refresh), new RefreshEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.interest_rates_screen_time), new ScreenEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.laboratory), new LaboratoryEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.help_and_feedback), new FeedbackEvent());
        }
    };

    public Map<String, Object> getItemEvent() {
        return itemEvent;
    }

    public List<SettingItemData> getItemData() {
        return itemData;
    }

    public void setItemData(List<SettingItemData> itemData) {
        this.itemData = itemData;
    }

    public void loadSettingData() {
        loadSettingItem();
        loadDeviceInfo();
    }

    private void loadDeviceInfo() {
        deviceModel.set(Build.MODEL);
        deviceVersion.set(String.format(JDReadApplication.getInstance().getString(R.string.device_setting_version_number),Build.DISPLAY));
        deviceSerial.set(String.format(JDReadApplication.getInstance().getString(R.string.device_setting_serial_number),Build.DISPLAY));
    }

    private void loadSettingItem() {
        String[] settingName = JDReadApplication.getInstance().getResources().getStringArray(R.array.setting_content);
        TypedArray typedArray = JDReadApplication.getInstance().getResources().obtainTypedArray(R.array.setting_drawables);
        int length = typedArray.length();
        int[] images = new int[length];
        for (int i = 0; i < length; i++) {
            images[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        for (int i = 0; i < settingName.length; i++) {
            SettingItemData data = new SettingItemData();
            data.setSettingName(settingName[i]);
            data.setSettingImage(images[i]);
            itemData.add(data);
        }
    }

    public void deviceConfig(){
        SettingBundle.getInstance().getEventBus().post(new ToDeviceConfigEvent());
    }
}
