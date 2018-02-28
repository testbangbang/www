package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Build;

import com.onyx.android.libsetting.util.StorageSizeUtil;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CopyrightNoticeEvent;
import com.onyx.jdread.setting.event.DeviceModelEvent;
import com.onyx.jdread.setting.event.ResetDeviceEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-2.
 */

public class DeviceInformationModel extends Observable {
    public final ObservableList<ItemModel> list = new ObservableArrayList<>();
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public DeviceInformationModel(EventBus eventBus) {
        titleBarModel.title.set(ResManager.getString(R.string.device_information));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
        list.add(createItemModel(eventBus, R.string.device_model_format, new String[]{String.format(ResManager.getString(R.string.setting_device_model_format), Build.MODEL)}, new DeviceModelEvent(), false));
        list.add(createItemModel(eventBus, R.string.device_version_format, new String[]{Build.DISPLAY}, null, false));
        list.add(createItemModel(eventBus, R.string.device_serial_format, new String[]{Build.SERIAL}, null, false));
        list.add(createItemModel(eventBus, R.string.device_mac_address_format,
                new String[]{NetworkUtil.getMacAddress(JDReadApplication.getInstance())}, null, false));
        list.add(createItemModel(eventBus, R.string.device_store_format,
                new Object[]{StorageSizeUtil.getFreeStorageInGB(), StorageSizeUtil.getTotalStorageAmountInGB()}, null, false));
        list.add(createItemModel(eventBus, R.string.copyright_notice_and_terms_of_service, null, new CopyrightNoticeEvent(), true));
        list.add(createItemModel(eventBus, R.string.reset, null, new ResetDeviceEvent(), true));
    }

    private ItemModel createItemModel(EventBus eventBus, int resId, Object[] formatArgs, Object event, boolean clickable) {
        if (formatArgs == null) {
            formatArgs = new Object[0];
        }
        ItemModel itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(ResManager.getString(resId), formatArgs));
        itemModel.event.set(event);
        itemModel.clickable.set(clickable);
        return itemModel;
    }

    public static class ItemModel extends BaseObservable {
        public final ObservableField<String> text = new ObservableField<>();
        public final ObservableField<Object> event = new ObservableField<>();
        public final ObservableBoolean clickable = new ObservableBoolean(false);
        private EventBus eventBus;

        public ItemModel(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        public void onClick() {
            if (event.get() != null) {
                eventBus.post(event.get());
            }
        }

        public boolean isClickable() {
            return clickable.get();
        }
    }
}
