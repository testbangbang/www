package com.onyx.jdread.main.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.PasswordIsCorrectEvent;
import com.onyx.jdread.setting.model.PswFailModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-2.
 */

public class LockScreenModel extends Observable {
    public final ObservableField<String> passwordEdit = new ObservableField<>();

    private PswFailModel pswFailModel;
    private String password;

    private EventBus eventBus;

    public LockScreenModel(EventBus eventBus) {
        this.eventBus = eventBus;
        pswFailModel = new PswFailModel(eventBus);
        password = PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.password_key, null);
    }

    public void unlockScreen() {
        unlock(passwordEdit.get());
    }

    public void unlockScreen(String password) {
        unlock(password);
    }

    private void unlock(String psw) {
        boolean valid = pswFailModel.checkUnlockFailData();
        if (!valid) {
            return;
        }
        if (StringUtils.isNotBlank(psw) && FileUtils.computeMD5(psw).equals(password)) {
            pswFailModel.saveUnlockFailData(null);
            eventBus.post(new PasswordIsCorrectEvent());
        } else {
            ToastUtil.showToast(ResManager.getString(R.string.wrong_password));
        }
    }
}
