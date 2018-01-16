package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToReadingToolsEvent;

import java.util.Observable;

/**
 * Created by hehai on 18-1-16.
 */

public class DictionaryModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableList<DictionaryItem> list = new ObservableArrayList<>();

    public DictionaryModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.dictionary));
        titleBarModel.backEvent.set(new BackToReadingToolsEvent());
    }

    public static class DictionaryItem extends BaseObservable {
        public final ObservableField<String> appName = new ObservableField<>();
        public final ObservableField<String> packageName = new ObservableField<>();

        public DictionaryItem(String appName, String packageName) {
            this.appName.set(appName);
            this.packageName.set(packageName);
        }
    }
}
