package com.onyx.jdread.personal.model;

import android.databinding.ObservableField;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.action.UserLoginAction;
import com.onyx.jdread.personal.event.BackToLoginEvent;
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
    public final ObservableField<String> errorMessage = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<SpannableString> registerTips = new ObservableField<>();
    public final ObservableField<SpannableString> retrieveTips = new ObservableField<>();
    public final ObservableField<Boolean> showLogin = new ObservableField<>(true);
    public final ObservableField<Boolean> showRegister = new ObservableField<>(false);
    public final ObservableField<Boolean> showRetrieve = new ObservableField<>(false);
    private EventBus eventBus;
    private final int ERROR_LOGIN_LIMIT = 3;
    private int count = 0;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public UserLoginViewModel() {
        title.set(ResManager.getString(R.string.user_login_jd_account));
        SpannableString registerWord = new SpannableString(ResManager.getString(R.string.register_with_qrcode));
        StyleSpan styleBold1 = new StyleSpan(Typeface.BOLD);
        StyleSpan styleBold2 = new StyleSpan(Typeface.BOLD);
        registerWord.setSpan(styleBold1, Constants.TIPS_START, Constants.TIPS_SUB_MIDDLE, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        registerWord.setSpan(styleBold2, Constants.TIPS_MIDDLE, Constants.TIPS_END, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        registerTips.set(registerWord);

        SpannableString retrieveWord = new SpannableString(ResManager.getString(R.string.find_password_with_qrcode));
        retrieveWord.setSpan(styleBold1, Constants.TIPS_START, Constants.TIPS_SUB_MIDDLE, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        retrieveWord.setSpan(styleBold2, Constants.TIPS_MIDDLE, Constants.TIPS_END, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        retrieveTips.set(retrieveWord);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void onLoginViewClick() {
        if (incAndCheckCount()) {
            return;
        }
        getEventBus().post(new HideSoftWindowEvent());
        UserLoginAction userLoginAction = new UserLoginAction(JDReadApplication.getInstance(), account.get(), password.get(), false);
        userLoginAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                getEventBus().post(new UserLoginEvent(account.get(), password.get()));
                count = 0;
            }

            @Override
            public void onFinally() {
                super.onFinally();
                loginButtonEnabled.set(true);
            }
        });
    }

    private boolean incAndCheckCount() {
        count++;
        if (count > ERROR_LOGIN_LIMIT) {
            retrievePassword();
            count = 0;
            return true;
        }
        return false;
    }

    public void onDeleteAccountViewClick() {
        account.set("");
    }

    public void cleanInput() {
        account.set("");
        password.set("");
        errorMessage.set("");
        count = 0;
        backToLogin();
    }

    public void register() {
        showLogin.set(false);
        showRetrieve.set(false);
        showRegister.set(true);
        title.set(ResManager.getString(R.string.register_jd_account));
    }

    public void backToLogin() {
        showLogin.set(true);
        showRegister.set(false);
        showRetrieve.set(false);
        title.set(ResManager.getString(R.string.user_login_jd_account));
    }

    public void retrievePassword() {
        showLogin.set(false);
        showRegister.set(false);
        showRetrieve.set(true);
        title.set(ResManager.getString(R.string.retrieve_password));
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

    public void onBackToLoginClick() {
        getEventBus().post(new BackToLoginEvent());
    }
}
