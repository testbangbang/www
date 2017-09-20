package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.ForgetPasswordView;
import com.onyx.android.dr.presenter.ForgetPasswordPresenter;
import com.onyx.android.dr.util.RegularUtil;
import com.onyx.android.sdk.data.model.v2.VerifyCode;
import com.onyx.android.sdk.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-9-18.
 */

public class ForgetPasswordActivity extends BaseActivity implements ForgetPasswordView {
    @Bind(R.id.forget_password_title)
    TextView forgetPasswordTitle;
    @Bind(R.id.login_title_line)
    View loginTitleLine;
    @Bind(R.id.phone_number)
    EditText phoneNumber;
    @Bind(R.id.get_verification_code)
    TextView getVerificationCode;
    @Bind(R.id.verification_code)
    EditText verificationCode;
    @Bind(R.id.set_new_password)
    EditText setNewPassword;
    @Bind(R.id.confirm_password)
    EditText confirmPassword;
    @Bind(R.id.change_password_result)
    TextView changePasswordResult;
    @Bind(R.id.forget_password_prev_button)
    TextView forgetPasswordPrevButton;
    @Bind(R.id.forget_password_next_button)
    TextView forgetPasswordNextButton;
    @Bind(R.id.get_verification_code_layout)
    View getVerificationLayout;
    @Bind(R.id.set_password_layout)
    View setPasswordLayout;
    @Bind(R.id.forget_password_name)
    TextView forgetPasswordName;
    private ForgetPasswordPresenter presenter;
    private int step = 0;
    private static final int STEP_FIRST = 1;
    private static final int STEP_SECOND = 2;
    private VerifyCode verifyCode;
    private long startGetVerifyTime;
    private long endGetVerifyTime;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        presenter = new ForgetPasswordPresenter(this);
    }

    @OnClick({R.id.get_verification_code, R.id.forget_password_prev_button, R.id.forget_password_next_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_verification_code:
                String phone = phoneNumber.getText().toString();
                if (StringUtils.isNotBlank(phone) && RegularUtil.isMobile(phone)) {
                    presenter.getVerificationCode(phone);
                    startGetVerifyTime = System.currentTimeMillis();
                } else {
                    phoneNumber.setText(Constants.EMPTY_STRING);
                    CommonNotices.showMessage(this, getString(R.string.please_fill_in_the_correct_phone_number));
                }
                break;
            case R.id.forget_password_prev_button:
                prevStep();
                break;
            case R.id.forget_password_next_button:
                nextStep();
                break;
        }
    }

    private boolean prevStep() {
        if (step == 0) {
            finish();
            return false;
        } else if (step == STEP_FIRST) {
            setPasswordLayout.setVisibility(View.GONE);
            getVerificationLayout.setVisibility(View.VISIBLE);
            step = 0;
        } else if (step == STEP_SECOND) {
            changePasswordResult.setVisibility(View.GONE);
            setPasswordLayout.setVisibility(View.VISIBLE);
            step = STEP_FIRST;
        }
        return true;
    }

    private void nextStep() {
        if (step == 0) {
            verify();
        } else if (step == STEP_FIRST) {
            setNewPassword();
        } else if (step == STEP_SECOND) {
            finish();
        }
    }

    private void setNewPassword() {
        if (StringUtils.isNullOrEmpty(setNewPassword.getText().toString())) {
            CommonNotices.showMessage(this, getString(R.string.new_password_must_be_filled));
            return;
        }

        if (StringUtils.isNullOrEmpty(confirmPassword.getText().toString()) ||
                !setNewPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            CommonNotices.showMessage(this, getString(R.string.password_input_is_inconsistent));
            return;
        }
        presenter.setNewPassword(verifyCode, setNewPassword.getText().toString());
    }

    private void verify() {
        if (StringUtils.isNullOrEmpty(verificationCode.getText().toString())) {
            CommonNotices.showMessage(this, getString(R.string.verification_code_must_be_filled));
            return;
        }
        endGetVerifyTime = System.currentTimeMillis();
        if (endGetVerifyTime - startGetVerifyTime < 60 * 1000) {
            if (verificationCode.getText().toString().equals(verifyCode.code)) {
                getVerificationLayout.setVisibility(View.GONE);
                setPasswordLayout.setVisibility(View.VISIBLE);
                forgetPasswordName.setVisibility(View.VISIBLE);
                step = STEP_FIRST;
            } else {
                CommonNotices.showMessage(this, getString(R.string.verification_code_error));
            }
        } else {
            CommonNotices.showMessage(this, getString(R.string.verification_code_expires));
        }
    }

    @Override
    public void setVerifyCode(VerifyCode verify) {
        this.verifyCode = verify;
        CommonNotices.showMessage(this, getString(R.string.verification_code_has_been_sent));
        forgetPasswordName.setText(String.format(getString(R.string.forget_password_user_name), verifyCode.name));
    }

    @Override
    public void setResult(String message) {
        setPasswordLayout.setVisibility(View.GONE);
        changePasswordResult.setVisibility(View.VISIBLE);
        step = STEP_SECOND;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (prevStep()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
