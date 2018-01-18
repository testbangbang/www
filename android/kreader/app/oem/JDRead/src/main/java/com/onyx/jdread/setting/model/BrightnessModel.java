package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
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

public class BrightnessModel extends BaseObservable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    private int progress;
    private int maxLight;
    private int numStar;
    private boolean isShow;

    private final List<Integer> mLightSteps;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyChange();
    }

    public int getMaxLight() {
        return maxLight;
    }

    public void setMaxLight(int maxLight) {
        this.maxLight = maxLight;
        notifyChange();
    }

    public int getNumStar() {
        return numStar;
    }

    public void setNumStar(int numStar) {
        this.numStar = numStar;
        notifyChange();
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
        notifyChange();
    }

    public BrightnessModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.brightness_control));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        mLightSteps = FrontLightController.getFrontLightValueList(JDReadApplication.getInstance());
        setMaxLight(mLightSteps.size() - 1);
        setNumStar(mLightSteps.size() - 1);
        setProgress(getIndex(FrontLightController.getBrightness(JDReadApplication.getInstance())));
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
        setProgress(0);
        FrontLightController.setBrightness(JDReadApplication.getInstance(), 0);
    }

    public void maxBrightness() {
        setProgress(getIndex(mLightSteps.get(mLightSteps.size() - 1)));
        FrontLightController.setBrightness(JDReadApplication.getInstance(), mLightSteps.get(mLightSteps.size() - 1));
    }
}
