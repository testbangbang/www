package com.onyx.edu.manager.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByAdminRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.LoginSuccessEvent;
import com.onyx.edu.manager.manager.ContentManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/5.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.edit_username)
    EditText usernameEdit;
    @Bind(R.id.edit_password)
    EditText passwordEdit;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    private void initView(ViewGroup parentView) {
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    onLoginClick();
                    return true;
                }
                return false;
            }
        });
    }

    private String getEditText(EditText editText) {
        return editText.getText().toString();
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        String username = getEditText(usernameEdit);
        if (StringUtils.isNullOrEmpty(username)) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.name_email_empty_tip);
            return;
        }
        String password = getEditText(passwordEdit);
        if (StringUtils.isNullOrEmpty(username)) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.password_empty_tip);
            return;
        }
        final LoginByAdminRequest loginRequest = new LoginByAdminRequest(BaseAuthAccount.create(username, password));
        AdminApplication.getCloudManager().submitRequest(getContext(), loginRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                NeoAccountBase account;
                if (e != null || (account = loginRequest.getNeoAccount()) == null) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), R.string.administrator_login_fail);
                    return;
                }
                afterLogin(account);
            }
        });
    }

    private void afterLogin(NeoAccountBase account) {
        ToastUtils.showToast(getContext().getApplicationContext(), R.string.administrator_login_success);
        ContentManager.saveAccount(getContext(), account);
        EventBus.getDefault().post(new LoginSuccessEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
