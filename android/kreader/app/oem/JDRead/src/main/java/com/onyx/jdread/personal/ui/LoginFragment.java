package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.LoginBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.UserLoginAction;
import com.onyx.jdread.personal.event.ForgetPasswordEvent;
import com.onyx.jdread.personal.event.UserLoginEvent;
import com.onyx.jdread.personal.event.UserRegisterJDAccountEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/2.
 */

public class LoginFragment extends BaseFragment {
    private LoginBinding binding;
    private boolean isHiddenState = true;
    private String account;
    private String password;
    private UserLoginViewModel userLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (LoginBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        PersonalDataBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.login));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.loginTitleBar.setTitleModel(titleModel);

        userLoginViewModel = PersonalDataBundle.getInstance().getPersonalViewModel().getUserLoginViewModel();
        binding.setModel(userLoginViewModel);
    }

    private void initListener() {
        if (binding.loginAccount != null) {
            Utils.showSoftWindow(binding.loginAccount);
            binding.loginAccount.requestFocus();
        }

        binding.loginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                account = s.toString().trim();
                setLoginButton();
            }
        });

        final Drawable[] comDrawablePwd = binding.switchAccountPassword.getCompoundDrawables();
        final float eyeWidth = comDrawablePwd[2].getBounds().width();
        final Drawable eyeOpen = getResources().getDrawable(R.mipmap.eye_open);
        eyeOpen.setBounds(comDrawablePwd[2].getBounds());

        binding.switchAccountPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString().trim();
                setLoginButton();
            }
        });

        binding.switchAccountPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float minWidth = v.getWidth() - eyeWidth - v.getPaddingRight();
                    float maxWidth = v.getWidth();
                    if (x > minWidth && x < maxWidth && y > 0 && y < v.getHeight()) {
                        isHiddenState = !isHiddenState;
                        if (isHiddenState) {
                            binding.switchAccountPassword.setCompoundDrawables(comDrawablePwd[0], comDrawablePwd[1],
                                    comDrawablePwd[2], comDrawablePwd[3]);
                            binding.switchAccountPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            binding.switchAccountPassword.setCompoundDrawables(comDrawablePwd[0], comDrawablePwd[1], eyeOpen, comDrawablePwd[3]);
                            binding.switchAccountPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                }
                return false;
            }
        });
    }

    private void setLoginButton() {
        if (!StringUtils.isNullOrEmpty(account) && !StringUtils.isNullOrEmpty(password)) {
            binding.switchAccountLogin.setEnabled(true);
        } else {
            binding.switchAccountLogin.setEnabled(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginEvent(UserLoginEvent event) {
        Utils.hideSoftWindow(getActivity());
        UserLoginAction userLoginAction = new UserLoginAction(getActivity(),event.account,event.password);
        userLoginAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                viewEventCallBack.viewBack();
            }
        });
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRegisterJDAccountEvent(UserRegisterJDAccountEvent event) {
        // TODO: 2018/1/2  
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onForgetPasswordEvent(ForgetPasswordEvent event) {
        // TODO: 2018/1/2  
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        Utils.hideSoftWindow(getActivity());
        viewEventCallBack.viewBack();
    }
}
