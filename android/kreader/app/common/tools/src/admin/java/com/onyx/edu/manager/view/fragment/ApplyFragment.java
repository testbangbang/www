package com.onyx.edu.manager.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.onyx.android.sdk.data.model.v2.AdminApplyModel;
import com.onyx.android.sdk.data.request.cloud.v2.AdministratorApplyRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AppApplication;
import com.onyx.edu.manager.R;

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

    public static ApplyFragment newInstance() {
        return new ApplyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @OnClick(R.id.button_apply)
    public void onApplyClick() {
        if (!checkEditInfoValid()) {
            ToastUtils.showToast(getContext().getApplicationContext(), "还有必填项没有填写");
            return;
        }
        startApplyAdmin();
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
        AdminApplyModel applyModel = createAdminApplyModel();
        AdministratorApplyRequest applyRequest = new AdministratorApplyRequest(applyModel);
        AppApplication.getCloudManager().submitRequest(getContext(), applyRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), "申请提交失败");
                    return;
                }
                afterApplyAdmin();
            }
        });
    }

    private void afterApplyAdmin() {
        ToastUtils.showToast(getContext().getApplicationContext(), "已经提交开通申请，等待审核批准");
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
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
