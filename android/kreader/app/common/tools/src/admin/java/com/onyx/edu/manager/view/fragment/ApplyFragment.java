package com.onyx.edu.manager.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.AdminApplyModel;
import com.onyx.android.sdk.data.model.v2.VerifyResult;
import com.onyx.android.sdk.data.request.cloud.v2.AdministratorApplyRequest;
import com.onyx.android.sdk.data.request.cloud.v2.PhoneVerifyRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.view.dialog.DialogHolder;
import com.onyx.edu.manager.view.ui.CountDownTimerButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/5.
 */
public class ApplyFragment extends Fragment {

    @Bind(R.id.edit_username)
    EditText etUsername;
    @Bind(R.id.edit_organization)
    EditText etOrganization;
    @Bind(R.id.edit_phone)
    EditText etPhone;

    @Bind(R.id.button_apply)
    CountDownTimerButton applyButton;

    private VerifyResult phoneVerifyResult;
    private boolean hasApplyPhoneVerified = false;

    public static ApplyFragment newInstance() {
        return new ApplyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    private void initView(ViewGroup parentView) {
        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    InputMethodUtils.hideInputKeyboard(getContext());
                    onApplyClick();
                    return true;
                }
                return false;
            }
        });
        initCountDownTimeView();
    }

    private void initCountDownTimeView() {
        applyButton.setNormalBackgroundRes(R.drawable.button_background);
        applyButton.setTimerBackgroundRes(R.drawable.button_disable_background);
        applyButton.setCountDownStringFormat(getString(R.string.count_down_timer_desc_format));
    }

    private void applyButtonStartCountDown() {
        applyButton.start(60 * 1000, 1000);
    }

    private void applyButtonStop() {
        if (applyButton != null) {
            applyButton.stopCountDownTimer();
        }
    }

    @OnClick(R.id.button_apply)
    public void onApplyClick() {
        if (!checkEditInfoValid()) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.required_fields_is_empty);
            return;
        }
        showPhoneVerifyDialog();
    }

    private void showPhoneVerifyDialog() {
        DialogHolder.getDialogBaseBuilder(getContext(), null, null)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .positiveText(R.string.verify)
                .content(String.format(getString(R.string.phone_verify_content), getEditText(etPhone)))
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(getString(R.string.phone_verify_input_hint), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (phoneVerifyResult == null || !StringUtils.getBlankStr(phoneVerifyResult.code).equals(input.toString())) {
                            ToastUtils.showToast(getContext().getApplicationContext(), R.string.verify_code_error);
                            return;
                        }
                        dialog.dismiss();
                        startApplyAdmin();
                    }
                })
                .keyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.do_apply)
                .neutralColorRes(R.color.colorAccent)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (isHasApplyPhoneVerified()) {
                            ToastUtils.showToast(getContext().getApplicationContext(), R.string.repeat_apply_warning);
                            return;
                        }
                        applyPhoneVerify();
                    }
                }).show();
    }

    private void setHasApplyPhoneVerified(boolean has) {
        this.hasApplyPhoneVerified = has;
    }

    private boolean isHasApplyPhoneVerified() {
        return hasApplyPhoneVerified;
    }

    private void applyPhoneVerify() {
        setHasApplyPhoneVerified(true);
        final PhoneVerifyRequest verifyRequest = new PhoneVerifyRequest(getEditText(etPhone));
        AdminApplication.getCloudManager().submitRequest(getContext(), verifyRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setHasApplyPhoneVerified(false);
                ToastUtils.showToast(getContext().getApplicationContext(), R.string.waiting_for_verify_code);
                phoneVerifyResult = verifyRequest.getVerifyResult();
                if (e != null || (phoneVerifyResult == null || StringUtils.isNullOrEmpty(phoneVerifyResult.code))) {
                    ToastUtils.showToast(getContext().getApplicationContext(), R.string.phone_verify_apply_error);
                }
            }
        });
    }

    private AdminApplyModel createAdminApplyModel() {
        AdminApplyModel applyModel = new AdminApplyModel();
        applyModel.name = getEditText(etUsername);
        applyModel.organization = getEditText(etOrganization);
        applyModel.phone = getEditText(etPhone);
        applyModel.mac = NetworkUtil.getMacAddress(getContext());
        return applyModel;
    }

    private void startApplyAdmin() {
        if (!NetworkUtil.isWiFiConnected(getContext())) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.network_is_not_connected);
            return;
        }
        final MaterialDialog dialog = DialogHolder.showProgressDialog(getContext(), getString(R.string.applying));
        final AdministratorApplyRequest applyRequest = new AdministratorApplyRequest(createAdminApplyModel());
        AdminApplication.getCloudManager().submitRequest(getContext(), applyRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialog.dismiss();
                if (e != null || !applyRequest.isApplySuccess()) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), R.string.apply_bind_fail);
                    return;
                }
                afterApplyAdmin();
            }
        });
    }

    private void afterApplyAdmin() {
        applyButtonStartCountDown();
        ToastUtils.showToast(getContext().getApplicationContext(), R.string.apply_bind_success);
    }

    private boolean checkEditInfoValid() {
        return isNotBlankContent(etUsername) && isNotBlankContent(etPhone) &&
                isNotBlankContent(etOrganization);
    }

    private boolean isNotBlankContent(EditText editText) {
        return StringUtils.isNotBlank(getEditText(editText));
    }

    private String getEditText(EditText editText) {
        return editText.getText().toString();
    }

    @Override
    public void onDestroy() {
        applyButtonStop();
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
