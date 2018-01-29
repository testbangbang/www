package com.onyx.jdread.personal.model;

import android.app.Activity;
import android.databinding.ObservableField;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.action.UserLoginAction;
import com.onyx.jdread.personal.event.ForgetPasswordEvent;
import com.onyx.jdread.personal.event.UserLoginEvent;
import com.onyx.jdread.personal.event.UserRegisterJDAccountEvent;
import com.onyx.jdread.util.Utils;

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
    private Activity context;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public UserLoginViewModel() {

    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void onLoginViewClick() {
        Utils.hideSoftWindow(context);
        UserLoginAction userLoginAction = new UserLoginAction(JDReadApplication.getInstance(),account.get(),password.get());
        userLoginAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                getEventBus().post(new UserLoginEvent(account.get(),password.get()));
            }

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                loginButtonEnabled.set(false);
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
        boolean showPassword = JDPreferenceManager.getBooleanValue(Constants.SP_KEY_SHOW_PASSWORD, false);
        isShowPassword.set(!showPassword);
        JDPreferenceManager.setBooleanValue(Constants.SP_KEY_SHOW_PASSWORD,!showPassword);
    }

    public void onRegisterViewClick() {
        getEventBus().post(new UserRegisterJDAccountEvent());
    }

    public void onForgetPasswordClick() {
        getEventBus().post(new ForgetPasswordEvent());
    }

    public void setContext(Activity context) {
        this.context = context;
    }
}
