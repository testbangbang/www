package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;
import android.support.annotation.Nullable;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by suicheng on 2018/2/11.
 */
public class PswFailModel extends BaseObservable {

    private EventBus eventBus;
    private PswFailData data;

    public PswFailModel(EventBus eventBus) {
        this.eventBus = eventBus;
        data = JDPreferenceManager.getPswFailData();
    }

    public boolean checkUnlockFailData() {
        if (data == null) {
            return true;
        }
        if (data.isCreatedTimeExpired(ResManager.getInteger(R.integer.password_unlock_fail_expired))) {
            saveUnlockFailData(null);
            return true;
        }
        if (checkDataTimesInvalid()) {
            return false;
        }
        return true;
    }

    public void saveUnlockFailData(@Nullable PswFailData newData) {
        JDPreferenceManager.setPswFailData(newData);
        data = ensureData(newData);
    }

    public PswFailData updateUnlockFailData() {
        data = ensureData(data);
        data.times--;
        saveUnlockFailData(data);
        if (!checkDataTimesInvalid()) {
            ToastUtil.showToast(R.string.password_unlock_current_times_format, data.times);
        }
        return data;
    }

    private boolean checkDataTimesInvalid() {
        if (data.isTimesInvalid()) {
            ToastUtil.showToast(R.string.password_unlock_times_format, ResManager.getInteger(R.integer.password_unlock_fail_count));
            return true;
        }
        return false;
    }

    private PswFailData ensureData(PswFailData data) {
        if (data == null) {
            data = PswFailData.create(ResManager.getInteger(R.integer.password_unlock_fail_count), new Date());
        }
        return data;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
