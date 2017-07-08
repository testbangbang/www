package com.onyx.edu.manager.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.AccountCommon;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.model.v2.UserInfoBind;
import com.onyx.android.sdk.data.request.cloud.v2.AccountBindByDeviceRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AppApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.DeviceUserInfoSwitchEvent;
import com.onyx.edu.manager.manager.ContentManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/7.
 */
public class DeviceBindCommitFragment extends Fragment {

    @Bind(R.id.layout_user_info)
    LinearLayout userInfoLayout;

    private EditText usernameEdit;
    private EditText phoneTvEdit;

    private TextView macAddressTv;
    private String macAddress;

    private boolean fromGroupSelect = false;
    private GroupUserInfo groupUserInfo;

    public static Fragment newInstance(GroupUserInfo groupUserInfo, boolean fromGroupSelect) {
        Bundle bundle = new Bundle();
        if (groupUserInfo != null) {
            bundle.putString(ContentManager.KEY_GROUP_USER_INFO, JSONObjectParseUtils.toJson(groupUserInfo));
        }
        bundle.putBoolean(ContentManager.KEY_GROUP_USER_INFO_FROM, fromGroupSelect);
        Fragment fragment = new DeviceBindCommitFragment();
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
        initData();
    }

    private void initView(View parentView) {
        macAddressTv = getMacAddressTv(parentView, R.id.layout_mac_address);
        usernameEdit = getUserInfoEditView(parentView, R.id.layout_username, getString(R.string.username_do));
        phoneTvEdit = getUserInfoEditView(parentView, R.id.layout_phone, getString(R.string.phone_do));
    }

    private void initData() {
        updateUserInfo();
        updateMacAddressView(getDeviceBind().mac);
    }

    private String getUsername() {
        GroupUserInfo groupUserInfo = getCommitEntity();
        if (groupUserInfo.user != null) {
            NeoAccountBase.parseName(groupUserInfo.user);
            return groupUserInfo.user.getName();
        } else {
            if (groupUserInfo.userBind == null) {
                return null;
            }
            return groupUserInfo.userBind.name;
        }
    }

    private String getPhone() {
        GroupUserInfo groupUserInfo = getCommitEntity();
        if (groupUserInfo.user != null) {
            return groupUserInfo.user.getPhone();
        } else {
            if (groupUserInfo.userBind == null) {
                return null;
            }
            return groupUserInfo.userBind.phone;
        }
    }

    private void updateUserInfo() {
        usernameEdit.setText(getUsername());
        phoneTvEdit.setText(getPhone());
        updateGroupsInfo();
    }

    private void updateGroupsInfo() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        List<CloudGroup> groupList = getCommitEntity().groups;
        if (CollectionUtils.isNullOrEmpty(groupList)) {
            return;
        }
        for (CloudGroup group : groupList) {
            userInfoLayout.addView(getGroupView(layoutInflater, group));
        }
    }

    private View getGroupView(LayoutInflater layoutInflater, CloudGroup group) {
        View view = layoutInflater.inflate(R.layout.layout_user_edit_info, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, (int) ScreenUtils.getDimenPixelSize(getContext(), 50));
        view.setLayoutParams(layoutParams);
        EditText editText = (EditText) view.findViewById(R.id.info_edit);
        editText.setEnabled(false);
        editText.setText(group.name);
        return view;
    }

    private void updateMacAddressView(String mac) {
        macAddressTv.setText(String.format(getString(R.string.mac_address_format), macAddress = mac));
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

    @OnClick(R.id.button_commit)
    public void onCommitClick() {
        AccountCommon accountCommon = new AccountCommon();
        accountCommon.name = getEditText(usernameEdit);
        accountCommon.phone = getEditText(phoneTvEdit);
        DeviceBind deviceBind = getDeviceBind();
        deviceBind.info = accountCommon;
        deviceBind.mac = macAddress;
        UserInfoBind userInfoBind = getCommitEntity().userBind;
        deviceBind.userInfoId = userInfoBind == null ? null : userInfoBind._id;
        if (!checkDeviceBindValid(deviceBind)) {
            ToastUtils.showToast(getContext().getApplicationContext(), "必填项没有完成...");
            return;
        }
        startAccountBindByDevice(getCommitEntity().groups, deviceBind);
    }

    private void startAccountBindByDevice(List<CloudGroup> groupList, DeviceBind deviceBind) {
        final AccountBindByDeviceRequest bindByDeviceRequest = new AccountBindByDeviceRequest(groupList,
                deviceBind);
        AppApplication.getCloudManager().submitRequest(getContext(), bindByDeviceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || (bindByDeviceRequest.getAccount()) == null) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), "设备绑定失败");
                    return;
                }
                processBindSuccess();
            }
        });
    }

    private void processBindSuccess() {
        ToastUtils.showToast(getContext().getApplicationContext(), "绑定成功");
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
        return groupUserInfo;
    }

    private DeviceBind getDeviceBind() {
        return getCommitEntity().device;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
