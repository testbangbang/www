package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.jdread.main.model.TitleBarModel;

import java.util.Observable;

/**
 * Created by hehai on 17-12-29.
 */

public class ContactUsModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public final ObservableField<String> feedback = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

}
