package com.onyx.jdread.setting.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.setting.event.AssociatedNotesToolsEvent;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.DictionaryToolsEvent;
import com.onyx.jdread.setting.event.TranslationToolsEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-2.
 */

public class ReadingToolsModel extends Observable {
    public final ObservableList<DeviceInformationModel.ItemModel> list = new ObservableArrayList<>();
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public ReadingToolsModel(EventBus eventBus) {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.reading_tools));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
        DeviceInformationModel.ItemModel itemModel = new DeviceInformationModel.ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.dictionary));
        itemModel.event.set(new DictionaryToolsEvent());
        list.add(itemModel);
        itemModel = new DeviceInformationModel.ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.translation));
        itemModel.event.set(new TranslationToolsEvent());
        list.add(itemModel);
        itemModel = new DeviceInformationModel.ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.associated_with_the_impression_notes));
        itemModel.event.set(new AssociatedNotesToolsEvent());
        list.add(itemModel);
        itemModel = new DeviceInformationModel.ItemModel(eventBus);
        itemModel.text.set(JDReadApplication.getInstance().getString(R.string.associated_with_the_email));
        itemModel.event.set(new AssociatedEmailToolsEvent());
        list.add(itemModel);
    }
}
