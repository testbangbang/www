package com.onyx.jdread.setting.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.event.ContactUsEvent;
import com.onyx.jdread.setting.event.FeedbackEvent;
import com.onyx.jdread.setting.event.ManualEvent;

import java.util.Observable;

/**
 * Created by hehai on 17-12-27.
 */

public class HelpModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public HelpModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.help_and_feedback));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
    }

    public void gotoManual() {
        SettingBundle.getInstance().getEventBus().post(new ManualEvent());
    }

    public void gotoFeedBack() {
        SettingBundle.getInstance().getEventBus().post(new FeedbackEvent());
    }

    public void gotoContactUs() {
        SettingBundle.getInstance().getEventBus().post(new ContactUsEvent());
    }
}
