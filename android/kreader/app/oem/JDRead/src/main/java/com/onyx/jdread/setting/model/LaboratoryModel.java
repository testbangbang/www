package com.onyx.jdread.setting.model;

import android.databinding.ObservableBoolean;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.event.ShowBackTabEvent;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 17-12-27.
 */

public class LaboratoryModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableBoolean showBack = new ObservableBoolean();

    public LaboratoryModel() {
        titleBarModel.title.set(ResManager.getString(R.string.laboratory));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        setShowBack(JDPreferenceManager.getBooleanValue(R.string.show_back_tab_key, false));
    }

    public void toggleBackTab() {
        EventBus.getDefault().post(new ShowBackTabEvent(showBack.get()));
    }

    public void setShowBack(boolean show) {
        showBack.set(show);
    }
}
