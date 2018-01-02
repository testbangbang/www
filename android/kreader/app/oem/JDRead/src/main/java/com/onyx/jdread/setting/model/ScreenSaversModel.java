package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CheckPicToScreenSaversEvent;

import java.util.Observable;

/**
 * Created by hehai on 18-1-1.
 */

public class ScreenSaversModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableList<ItemModel> picList = new ObservableArrayList<>();

    public ScreenSaversModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.screen_saver));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
    }

    public static class ItemModel extends BaseObservable {
        public final ObservableField<String> picPath = new ObservableField<>();
        public final ObservableBoolean isChecked = new ObservableBoolean(false);

        public void onClick(){
            if (!isChecked.get()){
                SettingBundle.getInstance().getEventBus().post(new CheckPicToScreenSaversEvent(picPath.get()));
            }
        }
    }
}
