package com.onyx.android.dr.activity;

import android.app.Activity;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.WifiSettingAdapter;
import com.onyx.android.dr.devicesetting.ui.dialog.WifiConnectedDialog;
import com.onyx.android.dr.devicesetting.ui.dialog.WifiLoginDialog;
import com.onyx.android.dr.devicesetting.ui.dialog.WifiSavedDialog;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.util.CommonUtil;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.android.sdk.wifi.WifiUtil;
import com.umeng.analytics.MobclickAgent;

import java.net.SocketException;
import java.util.List;

import static android.net.wifi.WifiInfo.LINK_SPEED_UNITS;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_BAND;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_IP_ADDRESS;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_LINK_SPEED;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_SECURITY_MODE;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_SIGNAL_LEVEL;
import static com.onyx.android.dr.devicesetting.data.util.Constant.ARGS_SSID;


/**
 * Created by huxiaomao on 2016/12/13.
 */

public class WifiActivity extends Activity implements View.OnClickListener {
    private ImageView close;
    private WifiAdmin wifiAdmin;
    private Switch wifiToggle;
    private PageRecyclerView wifiSettingRecyclerView;
    private WifiSettingAdapter wifiSettingAdapter;
    private TextView rescan;
    private float viewWidth = 0.0f;
    private float viewHeight = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity);
        initData();
        setWindowAttributes();
        initView();
        initWifi();
    }

    private void initData() {
        viewWidth = AppConfig.sharedInstance(this).getWifiSettingsPageViewSizeWidth();
        viewHeight = AppConfig.sharedInstance(this).getWifiSettingsPageViewSizeHeight();
    }

    private void initWifi() {
        wifiAdmin = new WifiAdmin(this, new WifiAdmin.Callback() {
            @Override
            public void onWifiStateChange(boolean isWifiEnable) {
                updateUI(isWifiEnable);
            }

            @Override
            public void onScanResultReady(List<AccessPoint> scanResult) {
                wifiSettingAdapter.setDataList(scanResult);
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WifiActivity");
        MobclickAgent.onResume(this);
        wifiAdmin.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WifiActivity");
        MobclickAgent.onPause(this);
        wifiAdmin.unregisterReceiver();
    }

    private void updateUI(boolean isWifiEnable) {
        wifiToggle.setChecked(isWifiEnable);
        wifiSettingRecyclerView.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
        if (!isWifiEnable) {
            wifiSettingAdapter.getDataList().clear();
            wifiSettingAdapter.notifyDataSetChanged();
        }
        rescan.setVisibility(isWifiEnable ? View.VISIBLE : View.GONE);
    }

    private void updateAccessPointDetailedState(NetworkInfo.DetailedState state) {
        if (wifiSettingAdapter.getDataList() != null) {
            for (AccessPoint accessPoint : wifiSettingAdapter.getDataList()) {
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
                        wifiSettingAdapter.getDataList().remove(accessPoint);
                        wifiSettingAdapter.getDataList().add(0, accessPoint);
                    }
                    break;
                }
            }
        }
    }

    public void initView() {
        close = (ImageView) findViewById(R.id.wifi_close);
        close.setOnClickListener(this);

        wifiToggle = (Switch) findViewById(R.id.wifi_setting_toggle);
        wifiToggle.setOnClickListener(this);

        wifiSettingRecyclerView = (PageRecyclerView) findViewById(R.id.wifi_setting_recycler_view);
        wifiSettingAdapter = new WifiSettingAdapter(this);

        wifiSettingRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        wifiSettingRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        wifiSettingRecyclerView.setAdapter(wifiSettingAdapter);

        wifiSettingAdapter.setOnItemClick(new PageRecyclerView.PageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object position) {
                if (wifiSettingAdapter.getDataList() != null) {
                    AccessPoint accessPoint = wifiSettingAdapter.getDataList().get((Integer) position);
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

        rescan = (TextView) findViewById(R.id.wifi_setting_rescan);
        rescan.setOnClickListener(this);

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
            args.putString(ARGS_BAND, WifiUtil.getBandString(WifiActivity.this,
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
        wifiLoginDialog.show(getFragmentManager());
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
        wifiSavedDialog.show(getFragmentManager());
    }

    public void setWindowAttributes() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * viewHeight);
        layoutParams.width = (int) (display.getWidth() * viewWidth);
        getWindow().setAttributes(layoutParams);

        setFinishOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_close:
                onCloseClick(v);
                break;
            case R.id.wifi_setting_toggle:
                onWifiSettingToggleClick(v);
                break;
            case R.id.wifi_setting_rescan:
                onWifiSettingRescanClick(v);
                break;
        }
    }

    public void onWifiSettingToggleClick(View v) {
        wifiAdmin.toggleWifi();
    }

    public void onWifiSettingRescanClick(View v) {
        wifiAdmin.triggerWifiScan();
    }

    public void onCloseClick(View v) {
        finish();
    }
}
