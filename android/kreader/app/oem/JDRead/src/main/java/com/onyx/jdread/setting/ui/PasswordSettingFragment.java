package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentPasswordSettingsBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.setting.event.BackToDeviceConfigEvent;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.model.PswSettingModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.view.NumberKeyboardPopWindow;
import com.onyx.jdread.setting.view.NumberKeyboardView;
import com.onyx.jdread.util.Utils;
import com.onyx.jdread.util.ViewCompatUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-12-28.
 */

public class PasswordSettingFragment extends BaseFragment {

    private FragmentPasswordSettingsBinding passwordSettingBinding;
    private NumberKeyboardPopWindow keyboardPopupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        return passwordSettingBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showInputKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
        dismissKeyboardPopupWindow();
    }


    private void showInputKeyboard() {
        final boolean encrypted = passwordSettingBinding.getPswSettingModel().encrypted.get();
        final EditText focusView = encrypted ? passwordSettingBinding.passwordUnEncryptEdit : passwordSettingBinding.passwordEncryptEdit;
        focusView.requestFocus();
        showPopupWindow(focusView, encrypted, getKeyboardListener());
    }

    private NumberKeyboardView.OnKeyboardListener getKeyboardListener() {
        return new NumberKeyboardView.OnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
            }

            @Override
            public void onDeleteKeyEvent() {
            }

            @Override
            public void onCustomKeyEvent() {
                processKeyboardCustomKey();
            }
        };
    }

    private void processKeyboardCustomKey() {
        boolean encrypted = passwordSettingBinding.getPswSettingModel().encrypted.get();
        if (encrypted) {
            processForgotPassword();
        } else {
            processNextEncrypt();
        }
    }

    private void processForgotPassword() {
        // TODO: 2018/2/8 add forgotPsw fragment using qrCode
    }

    private void processNextEncrypt() {
        int imeOptions = keyboardPopupWindow.getEditText().getImeOptions();
        if (imeOptions == EditorInfo.IME_ACTION_NEXT) {
            passwordSettingBinding.phoneEdit.requestFocus();
            passwordSettingBinding.phoneEdit.setSelection(passwordSettingBinding.phoneEdit.length());
        } else {
            passwordSettingBinding.getPswSettingModel().confirmPassword();
        }
    }

    private void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        passwordSettingBinding = FragmentPasswordSettingsBinding.inflate(inflater, container, false);
        PswSettingModel pswSettingModel = new PswSettingModel(SettingBundle.getInstance().getEventBus());
        passwordSettingBinding.passwordSettingsTitle.setTitleModel(pswSettingModel.titleBarModel);
        passwordSettingBinding.setPswSettingModel(pswSettingModel);
        initView();
    }

    private void initView() {
        ViewCompatUtil.disableEditShowSoftInput(passwordSettingBinding.passwordEncryptEdit,
                passwordSettingBinding.passwordUnEncryptEdit, passwordSettingBinding.phoneEdit);
        setEditOnFocusChangeListener(passwordSettingBinding.passwordEncryptEdit);
        setEditOnFocusChangeListener(passwordSettingBinding.passwordUnEncryptEdit);
        setEditOnFocusChangeListener(passwordSettingBinding.phoneEdit);
    }

    private void setEditOnFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showPopupWindow((EditText) v);
                }
            }
        });
    }

    private NumberKeyboardPopWindow showPopupWindow(EditText focusView) {
        return showPopupWindow(focusView,
                passwordSettingBinding.getPswSettingModel().encrypted.get(),
                getKeyboardListener());
    }

    private NumberKeyboardPopWindow showPopupWindow(EditText focusView, boolean encrypted, NumberKeyboardView.OnKeyboardListener listener) {
        if (keyboardPopupWindow == null) {
            keyboardPopupWindow = new NumberKeyboardPopWindow(getContext(), focusView, listener);
        }
        if (encrypted) {
            keyboardPopupWindow.getKeyboardView().setCustomText(getString(R.string.forgot_psw));
        } else {
            keyboardPopupWindow.getKeyboardView().setCustomDrawable(getResources().getDrawable(R.drawable.enter_icon));
        }
        keyboardPopupWindow.bindEdit(focusView, listener);
        keyboardPopupWindow.showAtBottomCenter(passwordSettingBinding.getRoot());
        return keyboardPopupWindow;
    }

    private void dismissKeyboardPopupWindow() {
        if (keyboardPopupWindow == null) {
            return;
        }
        keyboardPopupWindow.dismiss();
        keyboardPopupWindow = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToDeviceConfigFragment(BackToDeviceConfigFragment event) {
        Utils.hideSoftWindow(getActivity());
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToDeviceConfigEvent(BackToDeviceConfigEvent event) {
        ToastUtil.showToast(ResManager.getString(R.string.encryption_success));
        Utils.hideSoftWindow(getActivity());
        viewEventCallBack.viewBack();
    }
}
