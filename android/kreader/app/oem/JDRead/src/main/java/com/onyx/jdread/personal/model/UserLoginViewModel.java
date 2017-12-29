package com.onyx.jdread.personal.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.personal.event.CancelUserLoginDialogEvent;
import com.onyx.jdread.personal.event.UserLoginEvent;
import com.onyx.jdread.personal.event.UserRegisterJDAccountEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/27.
 */

public class UserLoginViewModel {

    public final ObservableField<String> account = new ObservableField<>();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<Boolean> isShowPassword = new ObservableField<>();
    private EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public UserLoginViewModel() {

    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void onLoginViewClick() {
        getEventBus().post(new UserLoginEvent(account.get(),password.get()));
    }

    public void onDeleteAccountViewClick() {
        account.set("");
    }

    public void cleanInput() {
        account.set("");
        password.set("");
    }

    public void onChangePasswordVisibleViewClick() {
        boolean showPassword = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), Constants.SP_KEY_SHOW_PASSWORD, false);
        isShowPassword.set(!showPassword);
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(),Constants.SP_KEY_SHOW_PASSWORD,!showPassword);
    }

    public void onRegisterViewClick() {
        getEventBus().post(new UserRegisterJDAccountEvent());
    }

    public void onCancelDialogViewClick() {
        getEventBus().post(new CancelUserLoginDialogEvent());
    }
}
