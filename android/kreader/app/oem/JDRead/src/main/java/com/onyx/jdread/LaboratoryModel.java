package com.onyx.jdread;

import android.databinding.ObservableBoolean;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.event.ShowBackTabEvent;
import com.onyx.jdread.model.TitleBarModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 17-12-27.
 */

public class LaboratoryModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel();
    public final ObservableBoolean showBack = new ObservableBoolean();

    public LaboratoryModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.laboratory));
        boolean show = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false);
        showBack.set(show);
    }

    public void toggleBackTab() {
        EventBus.getDefault().post(new ShowBackTabEvent(showBack.get()));
    }
}
