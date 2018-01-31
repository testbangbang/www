package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hehai on 18-1-5.
 */

public class BrightnessModel extends BaseObservable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    private int progress;
    private int maxLight;
    private int numStar;
    private boolean isShow;

    private final List<Integer> mLightSteps = new ArrayList<>();

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
        initLightSteps();
        setMaxLight(mLightSteps.size());
        setNumStar(mLightSteps.size());
        updateLight();
    }

    public void updateLight() {
        int index = getIndex(FrontLightController.getBrightness(JDReadApplication.getInstance()));
        setProgress(index == -1 ? 0 : index + 1);
    }

    private void initLightSteps() {
        mLightSteps.clear();
        int[] intArray = ResManager.getIntArray(R.array.light_step_array);
        for (int i = 0; i < intArray.length; i++) {
            mLightSteps.add(intArray[i]);
        }
    }

    private int getIndex(int val) {
        return Collections.binarySearch(mLightSteps, val);
    }

    public void setBrightness(int progress) {
        if (progress > 0) {
            FrontLightController.setBrightness(JDReadApplication.getInstance(), mLightSteps.get(progress - 1));
        } else {
            closeBrightness();
        }
    }

    public void closeBrightness() {
        setProgress(0);
        FrontLightController.setBrightness(JDReadApplication.getInstance(), 0);
    }

    public void maxBrightness() {
        setProgress(maxLight);
    }
}
