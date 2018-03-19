package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.view.dialog.WifiConnectedDialog;
import com.onyx.android.libsetting.view.dialog.WifiLoginDialog;
import com.onyx.android.libsetting.view.dialog.WifiSavedDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.android.sdk.wifi.WifiUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.WifiBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.setting.adapter.WifiSettingAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.setting.view.AddWIFIConfigurationDialog;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final int ADD_WIFI_TYPE = 0;
    private static final int LOGIN_WIFI_TYPE = 1;

    private WifiBinding binding;
    private WifiSettingAdapter wifiSettingAdapter;
    private WifiAdmin wifiAdmin;

    private int connectWifiType = -1;
    private String connectWifiKey = null;

    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
    private Timer scanTimer;
    private TimerTask wifiScanTimerTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (WifiBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_wifi, container, false);
        initView();
        initWifi();
        initData();
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
                    } else if (accessPoint.getSecurity() == 0) {
                        wifiAdmin.connectWifi(accessPoint);
                    } else if (accessPoint.getWifiConfiguration() == null) {
                        showLoginDialog(accessPoint);
                    } else {
                        showSaveDialog(accessPoint);
                    }
                }
            }
        });

        binding.wifiTitleBar.settingTitleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiAdmin.toggleWifi();
            }
        });

        binding.wifiTitleBar.settingTitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiAdmin.setWifiEnabled(true);
                addWifi();
            }
        });
    }

    private void addWifi() {
        final AddWIFIConfigurationDialog.DialogModel dialogModel = new AddWIFIConfigurationDialog.DialogModel();
        AddWIFIConfigurationDialog.Builder builder = new AddWIFIConfigurationDialog.Builder(getActivity(), dialogModel);
        final AddWIFIConfigurationDialog dialog = builder.create();
        dialogModel.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                dialog.dismiss();
            }
        });
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                addWifiImpl(dialogModel.ssid.get(), dialogModel.password.get(), dialogModel.type.get());
                dialog.dismiss();
            }
        });
        dialog.show();
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
                loginWifiImpl(accessPoint);
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
                loginWifiImpl(accessPoint);
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

    private void buildWifiScanTimerTask() {
        scanTimer = new Timer();
        wifiScanTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (wifiAdmin != null) {
                    wifiAdmin.triggerWifiScan();
                }
            }
        };
    }

    private void startScanTimer() {
        buildWifiScanTimerTask();
        scanTimer.schedule(wifiScanTimerTask, WIFI_RESCAN_INTERVAL_MS, WIFI_RESCAN_INTERVAL_MS);
    }

    private void stopScanTimer() {
        if (scanTimer != null) {
            scanTimer.cancel();
            wifiScanTimerTask.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanTimer();
    }


    @Override
    public void onResume() {
        super.onResume();
        wifiAdmin.registerReceiver();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
        startScanTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        wifiAdmin.unregisterReceiver();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
        stopScanTimer();
    }

    private void initWifi() {
        updateUI(NetworkUtil.isWifiEnabled(JDReadApplication.getInstance()));
        wifiAdmin = new WifiAdmin(JDReadApplication.getInstance(), new WifiAdmin.Callback() {
            @Override
            public void onWifiStateChange(boolean isWifiEnable, int wifiExtraState) {
                if (wifiExtraState != WifiManager.WIFI_STATE_ENABLING) {
                    updateUI(isWifiEnable);
                }
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                wifiSettingAdapter.setScanResult(scanResult);
                wifiSettingAdapter.notifyDataSetChanged();
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
        if (wifiSettingAdapter.getScanResult() != null) {
            for (AccessPoint accessPoint : wifiSettingAdapter.getScanResult()) {
                WifiConfiguration config = accessPoint.getWifiConfiguration();
                if (config == null) {
                    continue;
                }
                checkAccessPointFailStatus(accessPoint);
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
        binding.wifiTitleBar.settingTitleCheck.setChecked(isWifiEnable);
        binding.wifiRecycler.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
        if (!isWifiEnable) {
            wifiSettingAdapter.getScanResult().clear();
            wifiSettingAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setTitle(ResManager.getString(R.string.wireless_network));
        titleModel.setToggle(true);
        titleModel.setViewHistory(false);
        binding.wifiTitleBar.setTitleModel(titleModel);
    }

    private void initView() {
        binding.wifiRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.wifiRecycler.addItemDecoration(dividerItemDecoration);
        wifiSettingAdapter = new WifiSettingAdapter();
        binding.wifiRecycler.setAdapter(wifiSettingAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    private int getNetworkId(@NonNull AccessPoint accessPoint) {
        if (accessPoint.getWifiConfiguration() == null) {
            return -1;
        }
        return accessPoint.getWifiConfiguration().networkId;
    }

    private String getWifiAddKey(AccessPoint accessPoint) {
        return accessPoint.getScanResult().SSID + accessPoint.getSecurity();
    }

    private String getWifiConnectKey(AccessPoint accessPoint) {
        return accessPoint.getScanResult().SSID + accessPoint.getScanResult().BSSID + accessPoint.getSecurity();
    }

    private void addWifiImpl(String ssid, String password, int securityType) {
        setConnectWifi(ssid + securityType, ADD_WIFI_TYPE);
        wifiAdmin.addNetwork(wifiAdmin.createWifiConfiguration(ssid, password, securityType));
        updateUI(true);
    }

    private void loginWifiImpl(AccessPoint accessPoint) {
        setConnectWifi(getWifiConnectKey(accessPoint), LOGIN_WIFI_TYPE);
        wifiAdmin.connectWifi(accessPoint);
    }

    private void checkAccessPointFailStatus(AccessPoint accessPoint) {
        if (connectWifiType < 0 || StringUtils.isNullOrEmpty(connectWifiKey)) {
            return;
        }
        if (accessPoint.getScanResult() == null || accessPoint.getDisableReason() != WifiAdmin.DISABLED_AUTH_FAILURE) {
            return;
        }
        String key = "";
        switch (connectWifiType) {
            case ADD_WIFI_TYPE:
                key = getWifiAddKey(accessPoint);
                break;
            case LOGIN_WIFI_TYPE:
                key = getWifiConnectKey(accessPoint);
                break;
        }
        if (StringUtils.getBlankStr(key).equals(connectWifiKey)) {
            if (ResManager.getString(R.string.wifi_disabled_password_failure).equals(accessPoint.getSecurityString())) {
                ToastUtil.showToast(R.string.password_wrong);
                resetConnectWifi();
            }
        }
    }

    private void resetConnectWifi() {
        connectWifiType = -1;
        connectWifiKey = null;
    }

    private void setConnectWifi(String key, int type) {
        connectWifiType = type;
        connectWifiKey = key;
    }
}
