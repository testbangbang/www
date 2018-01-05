package com.onyx.jdread.setting.model;

import android.databinding.ObservableInt;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import java.util.Collections;
import java.util.List;
import java.util.Observable;

/**
 * Created by hehai on 18-1-5.
 */

public class BrightnessModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableInt progress = new ObservableInt();
    public final ObservableInt maxLight = new ObservableInt();
    public final ObservableInt numStar = new ObservableInt();

    private final List<Integer> mLightSteps;

    public BrightnessModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.brightness_control));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        mLightSteps = FrontLightController.getFrontLightValueList(JDReadApplication.getInstance());
        maxLight.set(mLightSteps.size() - 1);
        numStar.set(mLightSteps.size() - 1);
        progress.set(getIndex(FrontLightController.getBrightness(JDReadApplication.getInstance())));
    }

    private int getIndex(int val) {
        int index = Collections.binarySearch(mLightSteps, val);
        if (index == -1) {
            index = 0;
        } 
        return index;
    }

    public void setBrightness(int progress) {
        FrontLightController.setBrightness(JDReadApplication.getInstance(), mLightSteps.get(progress));
    }

    public void closeBrightness() {
        progress.set(0);
        FrontLightController.setBrightness(JDReadApplication.getInstance(), 0);
    }

    public void maxBrightness() {
        progress.set(getIndex(mLightSteps.get(mLightSteps.size() - 1)));
        FrontLightController.setBrightness(JDReadApplication.getInstance(), mLightSteps.get(mLightSteps.size() - 1));
    }
}
