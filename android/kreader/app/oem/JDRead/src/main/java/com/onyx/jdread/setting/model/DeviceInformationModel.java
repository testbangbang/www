package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Build;

import com.onyx.android.libsetting.util.StorageSizeUtil;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CopyrightNoticeEvent;
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
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.device_information));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
        ItemModel itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(JDReadApplication.getInstance().getString(R.string.device_model_format), Build.MODEL));
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(JDReadApplication.getInstance().getString(R.string.device_version_format), Build.DISPLAY));
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(JDReadApplication.getInstance().getString(R.string.device_serial_format), Build.SERIAL));
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(JDReadApplication.getInstance().getString(R.string.device_mac_address_format), NetworkUtil.getMacAddress(JDReadApplication.getInstance())));
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(String.format(JDReadApplication.getInstance().getString(R.string.device_store_format), StorageSizeUtil.getFreeStorageInGB(), StorageSizeUtil.getTotalStorageAmountInGB()));
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.copyright_notice_and_terms_of_service));
        itemModel.event.set(new CopyrightNoticeEvent());
        list.add(itemModel);
        itemModel = new ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.reset));
        itemModel.event.set(new ResetDeviceEvent());
        list.add(itemModel);
    }

    public static class ItemModel extends BaseObservable {
        public final ObservableField<String> text = new ObservableField<>();
        public final ObservableField<Object> event = new ObservableField<>();
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
            return event.get() != null;
        }
    }
}
