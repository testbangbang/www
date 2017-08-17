package com.onyx.edu.manager.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.AccountCommon;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.DeviceBindContainer;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.AccountCreateByDeviceRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountDeleteGroupsRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountUnBindByDeviceRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.DeviceUserInfoSwitchEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.view.activity.GroupListSelectActivity;
import com.onyx.edu.manager.view.dialog.DialogHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/7.
 */
public class DeviceBindingCommitFragment extends Fragment {
    private static final int REQUEST_CODE_GROUP_SELECT = 1000;

    @Bind(R.id.layout_user_info)
    LinearLayout userInfoLayout;
    @Bind(R.id.groups_layout)
    LinearLayout groupsLayout;
    @Bind(R.id.btn_unbind)
    Button unbindBtn;
    @Bind(R.id.btn_bind)
    Button bindBtn;

    private EditText usernameEdit;
    private EditText phoneTvEdit;

    private TextView macAddressTv;
    private String macAddress;

    private boolean fromGroupSelect = false;
    private GroupUserInfo groupUserInfo;

    private int oldGroupsCount = 0;

    public static Fragment newInstance(GroupUserInfo groupUserInfo, boolean fromGroupSelect) {
        Bundle bundle = new Bundle();
        if (groupUserInfo != null) {
            bundle.putString(ContentManager.KEY_GROUP_USER_INFO, JSONObjectParseUtils.toJson(groupUserInfo));
        }
        bundle.putBoolean(ContentManager.KEY_GROUP_USER_INFO_FROM, fromGroupSelect);
        Fragment fragment = new DeviceBindingCommitFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_bind_commit, container, false);
        ButterKnife.bind(this, view);

        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackClick();
                        return true;
                    }
                }
                return false;
            }
        });
        initData();
    }

    private void initToolbar(View parentView) {
        View view = parentView.findViewById(R.id.toolbar_header);
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.device_bind_info_edit_title);
    }

    private void initView(View parentView) {
        initToolbar(parentView);
        macAddressTv = getMacAddressTv(parentView, R.id.layout_mac_address);
        usernameEdit = getUserInfoEditView(parentView, R.id.layout_username, getString(R.string.username_do));
        phoneTvEdit = getUserInfoEditView(parentView, R.id.layout_phone, getString(R.string.phone_do));
    }

    private void initData() {
        updateUserInfo();
        updateBindButton();
        updateUnBindButton();
        updateMacAddressView(getDeviceBind());
    }

    private String getUsername() {
        if (getNeoAccount() == null) {
            return null;
        }
        NeoAccountBase.parseInfo(getNeoAccount());
        return getNeoAccount().getName();
    }

    private String getPhone() {
        if (getNeoAccount() != null) {
            return getNeoAccount().getPhone();
        } else {
            return null;
        }
    }

    private void updateUserInfo() {
        usernameEdit.setText(getUsername());
        phoneTvEdit.setText(getPhone());
        updateGroupsInfo();
    }

    private void updateUnBindButton() {
        if (getDeviceBind() == null || StringUtils.isNullOrEmpty(getDeviceBind().mac) || fromGroupSelect) {
            unbindBtn.setClickable(false);
            unbindBtn.setBackgroundResource(R.drawable.button_disable_background);
        }
    }

    private void updateBindButton() {
        if (getDeviceBind() == null || StringUtils.isNullOrEmpty(getDeviceBind().mac)) {
            bindBtn.setClickable(false);
            bindBtn.setBackgroundResource(R.drawable.button_disable_background);
        }
    }

    private void updateGroupsInfo() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        List<CloudGroup> groupList = getCommitEntity().groups;
        if (CollectionUtils.isNullOrEmpty(groupList)) {
            return;
        }
        for (CloudGroup group : groupList) {
            groupsLayout.addView(getGroupView(layoutInflater, group));
        }
    }

    private void updateGroupsInfo(CloudGroup group) {
        groupsLayout.addView(getGroupView(LayoutInflater.from(getContext()), group));
    }

    private View getGroupView(LayoutInflater layoutInflater, CloudGroup group) {
        View view = layoutInflater.inflate(R.layout.layout_swipe_user_edit_info, null);
        bindSwipeLayoutDeleteView(view, group);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, (int) ScreenUtils.getDimenPixelSize(getContext(), 50));
        view.setLayoutParams(layoutParams);
        EditText editText = (EditText) view.findViewById(R.id.info_edit);
        editText.setEnabled(false);
        editText.setText(group.name);
        return view;
    }

    private void bindSwipeLayoutDeleteView(View viewGroup, final CloudGroup group) {
        View view = viewGroup.findViewById(R.id.item_delete);
        view.setTag(group);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupRemoveConfirmDialog(group);
            }
        });
    }

    private void showGroupRemoveConfirmDialog(final CloudGroup group) {
        DialogHolder.showAlertDialog(getContext(), null, getString(R.string.group_delete_alert_message),
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        processRemoveGroup(group);
                    }
                });
    }

    private void processRemoveGroup(CloudGroup group) {
        int index = -1;
        List<CloudGroup> groupList = getCommitEntity().groups;
        for (int i = 0; i < groupList.size(); i++) {
            CloudGroup compareGroup = groupList.get(i);
            if (compareGroup._id.equals(group._id)) {
                index = i;
            }
        }
        if (index < 0) {
            return;
        }
        if (index >= oldGroupsCount) {
            removeGroup(index);
        } else {
            postRemoveGroupRequest(index);
        }
    }

    private void postRemoveGroupRequest(final int index) {
        final MaterialDialog dialog = DialogHolder.showProgressDialog(getContext(), getString(R.string.loading));
        List<String> list = new ArrayList<>();
        list.add(getCommitEntity().groups.get(index)._id);
        final AccountDeleteGroupsRequest deleteRequest = new AccountDeleteGroupsRequest(getNeoAccount(), list);
        AdminApplication.getCloudManager().submitRequest(getContext().getApplicationContext(), deleteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialog.dismiss();
                boolean success = deleteRequest.isSuccessful();
                ToastUtils.showToast(getContext().getApplicationContext(), success ? R.string.delete_success :
                        R.string.delete_fail);
                if (!success) {
                    return;
                }
                removeGroup(index);
                oldGroupsCount--;
            }
        });
    }

    private void removeGroup(int index) {
        getCommitEntity().groups.remove(index);
        groupsLayout.removeViewAt(index);
    }

    private void updateMacAddressView(final DeviceBind deviceBind) {
        if (deviceBind == null || StringUtils.isNullOrEmpty(deviceBind.mac)) {
            return;
        }
        macAddressTv.setText(String.format(getString(R.string.mac_address_format), macAddress = deviceBind.mac));
    }

    private TextView getMacAddressTv(View parentView, int parentId) {
        ViewGroup viewGroup = (ViewGroup) parentView.findViewById(parentId);
        return (TextView) viewGroup.findViewById(R.id.tv_mac_info);
    }

    private EditText getUserInfoEditView(View parentView, int parentId, String infoLabel) {
        ViewGroup viewGroup = (ViewGroup) parentView.findViewById(parentId);
        TextView infoLabelView = (TextView) viewGroup.findViewById(R.id.info_label);
        infoLabelView.setText(infoLabel);
        return (EditText) viewGroup.findViewById(R.id.info_edit);
    }

    @OnClick(R.id.btn_bind)
    public void onDeviceBindClick() {
        AccountCommon accountCommon = new AccountCommon();
        accountCommon.name = getEditText(usernameEdit);
        accountCommon.phone = getEditText(phoneTvEdit);
        DeviceBind deviceBind = getDeviceBind();
        deviceBind.info = accountCommon;
        deviceBind.mac = macAddress;
        if (!checkDeviceBindValid(deviceBind)) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.required_fields_is_empty);
            return;
        }
        if (CollectionUtils.isNullOrEmpty(getCommitEntity().groups)) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.group_has_no_selection);
            return;
        }
        createAccountByDevice(deviceBind);
    }

    @OnClick(R.id.btn_unbind)
    public void onDeviceUnbindClick() {
        showAccountUnbindDialog();
    }

    @OnClick(R.id.group_add)
    public void onGroupAddClick() {
        startActivityForResult(new Intent(getContext(), GroupListSelectActivity.class), REQUEST_CODE_GROUP_SELECT);
    }

    private void createAccountByDevice(DeviceBind deviceBind) {
        NeoAccountBase account = getNeoAccount();
        if (account == null) {
            account = new NeoAccountBase();
        }
        DeviceBindContainer bindContainer = new DeviceBindContainer();
        bindContainer.deviceBind = deviceBind;
        bindGroupsToContainerUser(bindContainer, account);
        final AccountCreateByDeviceRequest createByDeviceRequest = new AccountCreateByDeviceRequest(bindContainer);
        AdminApplication.getCloudManager().submitRequest(getContext(), createByDeviceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || (createByDeviceRequest.getAccount()) == null) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), R.string.device_bind_fail);
                    return;
                }
                processBindSuccess();
            }
        });
    }

    private void bindGroupsToContainerUser(DeviceBindContainer bindContainer, NeoAccountBase account) {
        bindContainer.user = account;
        List<CloudGroup> groupList = getCommitEntity().groups;
        if (CollectionUtils.isNullOrEmpty(groupList)) {
            return;
        }
        if (bindContainer.user.groups == null) {
            bindContainer.user.groups = new ArrayList<>();
        }
        for (CloudGroup cloudGroup : groupList) {
            bindContainer.user.groups.add(cloudGroup._id);
        }
    }

    private void showAccountUnbindDialog() {
        DialogHolder.showAlertDialog(getContext(), null, getString(R.string.user_device_unbind_confirm_content),
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startAccountUnbindByDevice();
                    }
                });
    }

    private void startAccountUnbindByDevice() {
        final AccountUnBindByDeviceRequest unBindByDeviceRequest = new AccountUnBindByDeviceRequest(getNeoAccount(),
                getDeviceBind());
        AdminApplication.getCloudManager().submitRequest(getContext(), unBindByDeviceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !unBindByDeviceRequest.isSuccessResult()) {
                    ToastUtils.showToast(getContext().getApplicationContext(), R.string.device_unbind_fail);
                    return;
                }
                processUnBindSuccess();
            }
        });
    }

    private void processBindSuccess() {
        finishWithToast(getString(R.string.device_bind_success));
    }

    private void processUnBindSuccess() {
        finishWithToast(getString(R.string.device_unbind_success));
    }

    private void finishWithToast(String toast) {
        ToastUtils.showToast(getContext().getApplicationContext(), toast);
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private boolean checkDeviceBindValid(DeviceBind deviceBind) {
        if (deviceBind == null) {
            return false;
        }
        if (deviceBind.info == null || StringUtils.isNullOrEmpty(deviceBind.info.name)
                || StringUtils.isNullOrEmpty(deviceBind.info.phone)
                ) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.toolbar_back)
    public void onBackClick() {
        if (fromGroupSelect) {
            EventBus.getDefault().post(new DeviceUserInfoSwitchEvent());
        } else {
            getActivity().finish();
        }
    }

    private String getEditText(EditText editText) {
        return editText.getText().toString();
    }

    private GroupUserInfo getCommitEntity() {
        if (groupUserInfo != null) {
            return groupUserInfo;
        }
        fromGroupSelect = getArguments().getBoolean(ContentManager.KEY_GROUP_USER_INFO_FROM);
        groupUserInfo = JSONObjectParseUtils.parseObject(getArguments().getString(ContentManager.KEY_GROUP_USER_INFO),
                GroupUserInfo.class);
        if (groupUserInfo == null) {
            groupUserInfo = new GroupUserInfo();
        }
        oldGroupsCount = CollectionUtils.getSize(groupUserInfo.groups);
        return groupUserInfo;
    }

    private DeviceBind getDeviceBind() {
        return getCommitEntity().device;
    }

    private NeoAccountBase getNeoAccount() {
        return getCommitEntity().user;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GROUP_SELECT && resultCode == Activity.RESULT_OK) {
            CloudGroup group = JSONObjectParseUtils.parseObject(data.getStringExtra(ContentManager.KEY_GROUP_SELECT),
                    CloudGroup.class);
            if (group != null) {
                getCommitEntity().groups.add(group);
                updateGroupsInfo(group);
            }
        }
    }
}
