package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/20.
 */

public class SettingTitleModel extends BaseObservable {
    private EventBus eventBus;
    private String title;
    private boolean toggle;

    public SettingTitleModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyChange();
    }

    public boolean isToggle() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        notifyChange();
    }

    public void backSetting() {
        eventBus.post(new BackToSettingFragmentEvent());
    }
}
