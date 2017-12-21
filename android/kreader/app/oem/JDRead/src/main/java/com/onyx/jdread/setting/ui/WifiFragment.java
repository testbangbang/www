package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.view.dialog.WifiConnectedDialog;
import com.onyx.android.libsetting.view.dialog.WifiLoginDialog;
import com.onyx.android.libsetting.view.dialog.WifiSavedDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.android.sdk.wifi.WifiUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.WifiBinding;
import com.onyx.jdread.setting.adapter.WifiSettingAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.SocketException;
import java.util.List;

import static android.net.wifi.WifiInfo.LINK_SPEED_UNITS;
import static com.onyx.android.libsetting.util.Constant.ARGS_BAND;
import static com.onyx.android.libsetting.util.Constant.ARGS_IP_ADDRESS;
import static com.onyx.android.libsetting.util.Constant.ARGS_LINK_SPEED;
import static com.onyx.android.libsetting.util.Constant.ARGS_SECURITY_MODE;
import static com.onyx.android.libsetting.util.Constant.ARGS_SIGNAL_LEVEL;
import static com.onyx.android.libsetting.util.Constant.ARGS_SSID;

/**
 * Created by li on 2017/12/20.
 */

public class WifiFragment extends BaseFragment {
    private WifiBinding binding;
    private WifiSettingAdapter wifiSettingAdapter;
    private WifiAdmin wifiAdmin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (WifiBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_wifi, container, false);
        initView();
        initData();
        initWifi();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        wifiSettingAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (wifiSettingAdapter.getScanResult() != null) {
                    AccessPoint accessPoint = wifiSettingAdapter.getScanResult().get(position);
                    if (accessPoint.getWifiInfo() != null) {
                        showConnectDialog(accessPoint);
                    } else if (accessPoint.getWifiConfiguration() == null) {
                        showLoginDialog(accessPoint);
                    } else {
                        showSaveDialog(accessPoint);
                    }
                }
            }
        });

        binding.wifiTitleBar.settingTitleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiAdmin.toggleWifi();
            }
        });
    }

    private void showSaveDialog(final AccessPoint accessPoint) {
        WifiSavedDialog wifiSavedDialog = new WifiSavedDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        args.putString(ARGS_SIGNAL_LEVEL, wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        args.putString(ARGS_SECURITY_MODE, accessPoint.getSecurityMode());
        wifiSavedDialog.setArguments(args);
        wifiSavedDialog.setCallback(new WifiSavedDialog.Callback() {
            @Override
            public void onForgetAccessPoint() {
                wifiAdmin.forget(accessPoint);
                wifiAdmin.triggerWifiScan();
            }

            @Override
            public void onConnectToAccessPoint() {
                wifiAdmin.connectWifi(accessPoint);
            }
        });
        wifiSavedDialog.show(getActivity().getFragmentManager());
    }

    private void showLoginDialog(final AccessPoint accessPoint) {
        WifiLoginDialog wifiLoginDialog = new WifiLoginDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SECURITY_MODE, accessPoint.getSecurityMode());
        args.putString(ARGS_SIGNAL_LEVEL, wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        wifiLoginDialog.setArguments(args);
        wifiLoginDialog.setCallback(new WifiLoginDialog.Callback() {
            @Override
            public void onConnectToAccessPoint(String password) {
                accessPoint.setPassword(password);
                wifiAdmin.connectWifi(accessPoint);
            }
        });
        wifiLoginDialog.show(getActivity().getFragmentManager());
    }

    private void showConnectDialog(final AccessPoint accessPoint) {
        WifiConnectedDialog wifiConnectedDialog = new WifiConnectedDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        args.putString(ARGS_LINK_SPEED, accessPoint.getWifiInfo().getLinkSpeed() + LINK_SPEED_UNITS);
        try {
            args.putString(ARGS_IP_ADDRESS, wifiAdmin.getLocalIPAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        args.putString(ARGS_SECURITY_MODE, accessPoint.getSecurityMode());
        args.putString(ARGS_SIGNAL_LEVEL,
                wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.LOLLIPOP)) {
            args.putString(ARGS_BAND, WifiUtil.getBandString(JDReadApplication.getInstance(),
                    accessPoint.getWifiInfo().getFrequency()));
        }
        wifiConnectedDialog.setArguments(args);
        wifiConnectedDialog.setCallback(new WifiConnectedDialog.Callback() {
            @Override
            public void onForgetAccessPoint() {
                wifiAdmin.forget(accessPoint);
                wifiAdmin.triggerWifiScan();
            }
        });
        wifiConnectedDialog.show(getActivity().getFragmentManager());
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiAdmin.registerReceiver();
        SettingBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        wifiAdmin.unregisterReceiver();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    private void initWifi() {
        wifiAdmin = new WifiAdmin(JDReadApplication.getInstance(), new WifiAdmin.Callback() {
            @Override
            public void onWifiStateChange(boolean isWifiEnable, int wifiExtraState) {
                updateUI(isWifiEnable);
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                wifiSettingAdapter.setScanResult(scanResult);
            }

            @Override
            public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {
                updateAccessPointDetailedState(state);
                wifiSettingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {
                updateAccessPointDetailedState(state);
                wifiSettingAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateAccessPointDetailedState(NetworkInfo.DetailedState state) {
        if(wifiSettingAdapter.getScanResult() != null) {
            for (AccessPoint accessPoint : wifiSettingAdapter.getScanResult()) {
                WifiConfiguration config = accessPoint.getWifiConfiguration();
                if (config == null) {
                    continue;
                }
                if (accessPoint.getWifiConfiguration().networkId ==
                        wifiAdmin.getCurrentConnectionInfo().getNetworkId()) {
                    accessPoint.setDetailedState(state);
                    if (state == NetworkInfo.DetailedState.CONNECTED) {
                        accessPoint.setSecurityString(getString(R.string.wifi_connected));
                        accessPoint.updateWifiInfo();
                        wifiSettingAdapter.getScanResult().remove(accessPoint);
                        wifiSettingAdapter.getScanResult().add(0, accessPoint);
                    }
                    break;
                }
            }
        }
    }

    private void updateUI(boolean isWifiEnable) {
        binding.wifiTitleBar.settingTitleSwitch.setChecked(isWifiEnable);
        binding.wifiRecycler.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
        if (!isWifiEnable) {
            wifiSettingAdapter.getScanResult().clear();
            wifiSettingAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.wireless_network));
        titleModel.setToggle(true);
        binding.wifiTitleBar.setTitleModel(titleModel);
    }

    private void initView() {
        binding.wifiRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.wifiRecycler.addItemDecoration(dividerItemDecoration);
        wifiSettingAdapter = new WifiSettingAdapter();
        binding.wifiRecycler.setAdapter(wifiSettingAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        getViewEventCallBack().gotoView(SettingFragment.class.getName());
    }
}
