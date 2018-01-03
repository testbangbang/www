package com.onyx.jdread.setting.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.main.util.RegularUtil;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.utils.Constants;

import java.util.Observable;

/**
 * Created by hehai on 18-1-2.
 */

public class PswSettingModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableField<String> passwordEdit = new ObservableField<>();
    public final ObservableField<String> phoneEdit = new ObservableField<>();
    public final ObservableField<String> unlockPasswordEdit = new ObservableField<>();
    public final ObservableBoolean encrypted = new ObservableBoolean(false);
    private final String password;

    public PswSettingModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.password_setting));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
        password = PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.password_key, null);
        encrypted.set(StringUtils.isNotBlank(password));
    }

    public void confirmPassword() {
        String password = passwordEdit.get();
        if (StringUtils.isNullOrEmpty(password) || (password.length() < Constants.PASSWORD_MIN_LENGTH) || password.length() > Constants.PASSWORD_MAX_LENGTH) {
            ToastUtil.showToast(JDReadApplication.getInstance(), String.format(JDReadApplication.getInstance().getString(R.string.password_format_error), Constants.PASSWORD_MIN_LENGTH, Constants.PASSWORD_MAX_LENGTH));
            return;
        }

        if (!RegularUtil.isMobile(phoneEdit.get())) {
            ToastUtil.showToast(JDReadApplication.getInstance(), R.string.phone_number_format_error);
            return;
        }
        // TODO: 18-1-2 save password
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), R.string.password_key, FileUtils.computeMD5(passwordEdit.get()));
        encrypted.set(true);
    }

    public void unlockPassword() {
        String unlockPassword = unlockPasswordEdit.get();
        if (StringUtils.isNotBlank(unlockPassword) && FileUtils.computeMD5(unlockPassword).equals(password)) {
            // TODO: 18-1-2 unlock password
            PreferenceManager.setStringValue(JDReadApplication.getInstance(), R.string.password_key, "");
            encrypted.set(false);
        } else {
            ToastUtil.showToast(JDReadApplication.getInstance(), R.string.wrong_password);
        }
    }
}
