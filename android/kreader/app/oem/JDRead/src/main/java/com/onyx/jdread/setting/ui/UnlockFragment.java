package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentUnlockBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.LockScreenModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.view.NumberKeyboardPopWindow;
import com.onyx.jdread.setting.view.NumberKeyboardView;
import com.onyx.jdread.util.ViewCompatUtil;

/**
 * Created by suicheng on 2018/2/8.
 */
public class UnlockFragment extends BaseFragment {

    private FragmentUnlockBinding dataBinding;
    private NumberKeyboardPopWindow keyboardPopupWindow;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(inflater, container);
        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        dataBinding = FragmentUnlockBinding.inflate(inflater, container, false);
        dataBinding.setScreenLockModel(new LockScreenModel(SettingBundle.getInstance().getEventBus()));
        ViewCompatUtil.disableEditShowSoftInput(dataBinding.passwordUnEncryptEdit);
        dataBinding.passwordUnEncryptEdit.requestFocus();
    }

    private NumberKeyboardPopWindow showPopupWindow(EditText focusView) {
        return showPopupWindow(focusView, new NumberKeyboardView.OnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
            }

            @Override
            public void onDeleteKeyEvent() {
            }

            @Override
            public void onCustomKeyEvent() {
                processPasswordForgot();
            }
        });
    }

    private void processPasswordForgot() {
        viewEventCallBack.gotoView(PasswordFindFragment.class.getName());
        dismissKeyboardPopupWindow();
    }

    private NumberKeyboardPopWindow showPopupWindow(EditText focusView, NumberKeyboardView.OnKeyboardListener listener) {
        if (keyboardPopupWindow == null) {
            keyboardPopupWindow = new NumberKeyboardPopWindow(getContext(), focusView, listener);
        }
        keyboardPopupWindow.getKeyboardView().setCustomText(getString(R.string.forgot_psw));
        keyboardPopupWindow.showAtBottomCenter(dataBinding.getRoot());
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
        InputMethodUtils.hideInputKeyboard(getActivity());
        dataBinding.passwordUnEncryptEdit.post(new Runnable() {
            @Override
            public void run() {
                dataBinding.passwordUnEncryptEdit.requestFocus();
                showPopupWindow(dataBinding.passwordUnEncryptEdit);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissKeyboardPopupWindow();
    }
}
