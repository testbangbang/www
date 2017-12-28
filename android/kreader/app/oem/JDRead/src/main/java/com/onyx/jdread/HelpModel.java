package com.onyx.jdread;

import com.onyx.jdread.model.TitleBarModel;

import java.util.Observable;

/**
 * Created by hehai on 17-12-27.
 */

public class HelpModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel();

    public HelpModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.help_and_feedback));
    }

    public void gotoManual() {

    }

    public void gotoFeedBack() {

    }

    public void gotoContactUs() {

    }
}
