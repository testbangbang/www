package com.onyx.android.libsetting.view.activity;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.databinding.ActivityWifiSettingBinding;
import com.onyx.android.libsetting.databinding.WifiInfoItemBinding;
import com.onyx.android.libsetting.util.SettingRecyclerViewUtil;
import com.onyx.android.libsetting.view.BindingViewHolder;
import com.onyx.android.libsetting.view.PageRecyclerViewItemClickListener;
import com.onyx.android.libsetting.view.SettingPageAdapter;
import com.onyx.android.libsetting.view.dialog.WifiConnectedDialog;
import com.onyx.android.libsetting.view.dialog.WifiLoginDialog;
import com.onyx.android.libsetting.view.dialog.WifiSavedDialog;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.android.sdk.wifi.WifiUtil;

import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

import static android.net.wifi.WifiInfo.LINK_SPEED_UNITS;
import static com.onyx.android.libsetting.util.Constant.ARGS_BAND;
import static com.onyx.android.libsetting.util.Constant.ARGS_IP_ADDRESS;
import static com.onyx.android.libsetting.util.Constant.ARGS_LINK_SPEED;
import static com.onyx.android.libsetting.util.Constant.ARGS_SECURITY_MODE;
import static com.onyx.android.libsetting.util.Constant.ARGS_SIGNAL_LEVEL;
import static com.onyx.android.libsetting.util.Constant.ARGS_SSID;

public class WifiSettingActivity extends OnyxAppCompatActivity {
    static final boolean DEBUG = true;

    static final String TAG = WifiSettingActivity.class.getSimpleName();
    static final String ACTION_WIFI_ENABLE = "android.intent.action.WIFI_ENABLE";
    ActivityWifiSettingBinding binding;
    @Nullable
    WifiAdmin wifiAdmin = null;
    OnyxPageDividerItemDecoration itemDecoration;
    SettingPageAdapter<WifiResultItemViewHolder, AccessPoint> adapter;

    final static String[] WIFI_PERMS = {Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int WIFI_PERMS_REQUEST_CODE = 1;

    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private Timer scanTimer;
    private TimerTask wifiScanTimerTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        if (EasyPermissions.hasPermissions(this, WIFI_PERMS)) {
            initWifiAdmin();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.wifi_rational), WIFI_PERMS_REQUEST_CODE, WIFI_PERMS);
        }
    }

    private void logScanResult(List<AccessPoint> scanResult) {
        Set<String> set = new HashSet<>();
        for (AccessPoint ap:scanResult){
            if (ap == null || ap.getScanResult() == null) {
                Log.e(TAG, "null ap found");
                continue;
            }
            final ScanResult sr = ap.getScanResult();
            Log.e(TAG, "AccessPoint SSID:" + sr.SSID + " BSSID: " + sr.BSSID);
            if (set.contains(sr.SSID)) {
                Log.e(TAG, "Duplicated id found:" + sr.SSID + " bssid: " + sr.BSSID);
            }
            set.add(sr.SSID);
        }
        Log.e(TAG, "Result Size:" + scanResult.size());
    }

    private void initWifiAdmin() {
        wifiAdmin = new WifiAdmin(this, new WifiAdmin.Callback() {
            @Override
            public void onWifiStateChange(boolean isWifiEnable, int wifiExtraState) {
                updateUI(isWifiEnable,wifiExtraState);
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                adapter.setDataList(scanResult);
                if (DEBUG){
                    logScanResult(scanResult);
                }
                updateSummary(true);
                binding.wifiScanResultRecyclerView.notifyDataSetChanged();
            }

            @Override
            public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {
                updateAccessPointDetailedState(state);
                binding.wifiScanResultRecyclerView.notifyDataSetChanged();
            }

            @Override
            public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {
                updateAccessPointDetailedState(state);
                binding.wifiScanResultRecyclerView.notifyDataSetChanged();
            }
        });
        processAction();
    }

    private void processAction() {
        if (ACTION_WIFI_ENABLE.equals(getIntent().getAction())) {
            if (wifiAdmin != null) {
                wifiAdmin.setWifiEnabled(true);
            }
        }
    }

    private void updateAccessPointDetailedState(NetworkInfo.DetailedState state) {
        for (AccessPoint accessPoint : adapter.getDataList()) {
            WifiConfiguration config = accessPoint.getWifiConfiguration();
            if (config == null) {
                continue;
            }
            if (wifiAdmin != null && accessPoint.getWifiConfiguration().networkId ==
                    wifiAdmin.getCurrentConnectionInfo().getNetworkId()) {
                accessPoint.setDetailedState(state);
                if (state == NetworkInfo.DetailedState.CONNECTED) {
                    accessPoint.setSecurityString(getString(R.string.wifi_connected));
                    accessPoint.updateWifiInfo();
                    adapter.getDataList().remove(accessPoint);
                    adapter.getDataList().add(0, accessPoint);
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wifiAdmin != null) {
            wifiAdmin.registerReceiver();
        }
        if (SettingConfig.sharedInstance(this).isEnableAutoWifiReScan()) {
            buildWifiScanTimerTask();
            scanTimer.schedule(wifiScanTimerTask, WIFI_RESCAN_INTERVAL_MS, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wifiAdmin != null) {
            wifiAdmin.unregisterReceiver();
        }
        if (SettingConfig.sharedInstance(this).isEnableAutoWifiReScan() && scanTimer != null) {
            scanTimer.cancel();
            wifiScanTimerTask.cancel();
        }
    }

    private void updateUI(boolean isWifiEnable, int wifiExtraState) {
        binding.wifSwitch.setChecked(isWifiEnable);
        switch (wifiExtraState){
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_ENABLED:
                binding.wifiToggleButton.setEnabled(true);
                break;
        }
        binding.wifiScanResultRecyclerView.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
        if (!isWifiEnable) {
            adapter.getDataList().clear();
            binding.wifiScanResultRecyclerView.notifyDataSetChanged();
        }
        updateSummary(isWifiEnable);
        binding.rescanBtn.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
    }

    private void updateSummary(boolean isWifiEnable) {
        binding.textViewWifiSummary.setText(isWifiEnable ?
                getString(R.string.current_available_wifi_count, adapter.getDataCount())
                : getString(R.string.open_wifi));
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wifi_setting);
        initSupportActionBarWithCustomBackFunction();
        binding.wifiToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiAdmin != null) {
                    wifiAdmin.toggleWifi();
                    binding.wifiToggleButton.setEnabled(false);
                }
            }
        });
        binding.rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiAdmin != null) {
                    wifiAdmin.triggerWifiScan();
                }
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        PageRecyclerView resultRecyclerView = binding.wifiScanResultRecyclerView;
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(new DisableScrollLinearManager(this));
        buildAdapter();
        itemDecoration = new OnyxPageDividerItemDecoration(this, OnyxPageDividerItemDecoration.VERTICAL);
        resultRecyclerView.addItemDecoration(itemDecoration);
        itemDecoration.setActualChildCount(adapter.getRowCount());
        resultRecyclerView.setItemDecorationHeight(itemDecoration.getDivider().getIntrinsicHeight());
        resultRecyclerView.setAdapter(adapter);
    }

    private void buildAdapter() {
        adapter = new SettingPageAdapter<WifiResultItemViewHolder, AccessPoint>() {
            @Override
            public int getRowCount() {
                return WifiSettingActivity.this.getResources().getInteger(R.integer.wifi_per_page_item_count);
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public WifiResultItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new WifiResultItemViewHolder(WifiInfoItemBinding.inflate(getLayoutInflater(), parent, false));
            }

            @Override
            public void onPageBindViewHolder(WifiResultItemViewHolder holder, int position) {
                super.onPageBindViewHolder(holder, position);
                SettingRecyclerViewUtil.updateItemDecoration(pageRecyclerView, this, itemDecoration);
                holder.bindTo(getDataList().get(position));
            }
        };

        adapter.setItemClickListener(new PageRecyclerViewItemClickListener<AccessPoint>() {
            @Override
            public void itemClick(AccessPoint accessPoint) {
                if (accessPoint.isConnected()) {
                    showConnectDialog(accessPoint);
                } else if (accessPoint.getWifiConfiguration() == null) {
                    showLoginDialog(accessPoint);
                } else {
                    showSaveDialog(accessPoint);
                }
            }
        });
    }

    private void showConnectDialog(final AccessPoint accessPoint) {
        WifiConnectedDialog wifiConnectedDialog = new WifiConnectedDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        args.putString(ARGS_LINK_SPEED, accessPoint.getWifiInfo().getLinkSpeed() + LINK_SPEED_UNITS);
        try {
            if (wifiAdmin != null) {
                args.putString(ARGS_IP_ADDRESS, wifiAdmin.getLocalIPAddress());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        args.putString(ARGS_SECURITY_MODE, accessPoint.getSecurityMode());
        args.putString(ARGS_SIGNAL_LEVEL,
                wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.LOLLIPOP)) {
            args.putString(ARGS_BAND, WifiUtil.getBandString(WifiSettingActivity.this,
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
        wifiConnectedDialog.show(getFragmentManager());
    }

    private void showLoginDialog(final AccessPoint accessPoint) {
        if (wifiAdmin.getSecurity(accessPoint.getScanResult()) == WifiAdmin.SECURITY_NONE) {
            wifiAdmin.connectWifi(accessPoint);
            return;
        }
        WifiLoginDialog wifiLoginDialog = new WifiLoginDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SECURITY_MODE, accessPoint.getSecurityMode());
        if (wifiAdmin != null) {
            args.putString(ARGS_SIGNAL_LEVEL, wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        }
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        wifiLoginDialog.setArguments(args);
        wifiLoginDialog.setCallback(new WifiLoginDialog.Callback() {
            @Override
            public void onConnectToAccessPoint(String password) {
                accessPoint.setPassword(password);
                wifiAdmin.connectWifi(accessPoint);
            }
        });
        wifiLoginDialog.show(getFragmentManager());
    }

    private void showSaveDialog(final AccessPoint accessPoint) {
        WifiSavedDialog wifiSavedDialog = new WifiSavedDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_SSID, accessPoint.getScanResult().SSID);
        if (wifiAdmin != null) {
            args.putString(ARGS_SIGNAL_LEVEL, wifiAdmin.getSignalString(accessPoint.getSignalLevel()));
        }
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
        wifiSavedDialog.show(getFragmentManager());
    }

    private class WifiResultItemViewHolder extends BindingViewHolder<WifiInfoItemBinding, AccessPoint> {
        WifiResultItemViewHolder(WifiInfoItemBinding binding) {
            super(binding);
        }

        public void bindTo(AccessPoint accessPoint) {
            mBinding.setAccessPoint(accessPoint);
            mBinding.executePendingBindings();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if (requestCode == WIFI_PERMS_REQUEST_CODE) {
            initWifiAdmin();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        if (requestCode == WIFI_PERMS_REQUEST_CODE) {
            showToast(R.string.no_wifi_permission, Toast.LENGTH_SHORT);
            /*
              onBackPressed() here may cause some illegal state.
              ref link:http://stackoverflow.com/a/38972502/4192473
            */
            try {
                onBackPressed();
            } catch (IllegalStateException e) {
                finish();
            }
        }
    }
}
