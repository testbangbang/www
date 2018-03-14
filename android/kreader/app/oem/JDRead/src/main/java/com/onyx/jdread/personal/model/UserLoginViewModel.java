package com.onyx.jdread.personal.model;

import android.databinding.ObservableField;
import android.text.TextUtils;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.UserLoginAction;
import com.onyx.jdread.personal.event.ForgetPasswordEvent;
import com.onyx.jdread.personal.event.HideSoftWindowEvent;
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
    public final ObservableField<Boolean> loginButtonEnabled = new ObservableField<>(true);
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
        getEventBus().post(new HideSoftWindowEvent());
        UserLoginAction userLoginAction = new UserLoginAction(JDReadApplication.getInstance(), account.get(), password.get(), false);
        userLoginAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                getEventBus().post(new UserLoginEvent(account.get(), password.get()));
            }

            @Override
            public void onFinally() {
                super.onFinally();
                loginButtonEnabled.set(true);
            }
        });
    }

    public void onDeleteAccountViewClick() {
        account.set("");
    }

    public void cleanInput() {
        account.set("");
        password.set("");
    }

    public void onChangePasswordVisibleViewClick() {
        isShowPassword.set(!isShowPassword.get());
    }

    public void onRegisterViewClick() {
        getEventBus().post(new UserRegisterJDAccountEvent());
    }

    public void onForgetPasswordClick() {
        getEventBus().post(new ForgetPasswordEvent());
    }
}
