package com.onyx.jdread.main.activity;

import android.databinding.DataBindingUtil;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.view.dialog.WifiConnectedDialog;
import com.onyx.android.libsetting.view.dialog.WifiLoginDialog;
import com.onyx.android.libsetting.view.dialog.WifiSavedDialog;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.sdk.wifi.WifiAdmin;
import com.onyx.android.sdk.wifi.WifiUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.StartBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.NetworkConnectedEvent;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.main.model.StartBundle;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.personal.action.SetReadPreferenceAction;
import com.onyx.jdread.personal.adapter.ReadPreferenceAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.SetReadPreferenceBean;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.setting.adapter.WifiSettingAdapter;
import com.onyx.jdread.setting.view.AddWIFIConfigurationDialog;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

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
 * Created by li on 2018/3/9.
 */

public class StartActivity extends AppCompatActivity {
    private StartBinding binding;
    private WifiSettingAdapter wifiSettingAdapter;
    private WifiAdmin wifiAdmin;
    private ReadPreferenceAdapter readPreferenceAdapter;
    private SystemBarModel systemBarModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiAdmin.registerReceiver();
        systemBarModel.registerReceiver(JDReadApplication.getInstance());
        Utils.ensureRegister(StartBundle.getInstance().getEventBus(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wifiAdmin.unregisterReceiver();
        systemBarModel.unRegisterReceiver(JDReadApplication.getInstance());
        Utils.ensureUnregister(StartBundle.getInstance().getEventBus(), this);
    }

    private void init() {
        checkGuide();
        initSystemBar();
        initWifi();
        initLogin();
        initCategory();
        initListener();
    }

    private void checkGuide() {
        String flag = JDPreferenceManager.getStringValue(Constants.IS_GUIDE, "");
        if (StringUtils.isNotBlank(flag)) {
            finishGuide();
        }
    }

    private void initSystemBar() {
        systemBarModel = MainBundle.getInstance().getSystemBarModel();
        binding.setSystemBarModel(systemBarModel);
    }

    private void initListener() {
        binding.startNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        wifiSettingAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (wifiSettingAdapter.getScanResult() != null) {
                    AccessPoint accessPoint = wifiSettingAdapter.getScanResult().get(position);
                    if (accessPoint.getSecurity() == 0) {
                        wifiAdmin.connectWifi(accessPoint);
                    } else if (accessPoint.getWifiInfo() != null) {
                        showConnectDialog(accessPoint);
                    } else if (accessPoint.getWifiConfiguration() == null) {
                        showLoginDialog(accessPoint);
                    } else {
                        showSaveDialog(accessPoint);
                    }
                }
            }
        });

        binding.startWifiAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiAdmin.setWifiEnabled(true);
                addWifi();
            }
        });

        binding.startSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGuide();
            }
        });
    }

    private void next() {
        if (binding.startWifiRecycler.getVisibility() == View.VISIBLE && !Utils.isNetworkConnected(StartActivity.this)) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        if (binding.startPreference.startPreference.getVisibility() == View.VISIBLE) {
            savePreference();
            return;
        }
        if (binding.startLogin.startLogin.getVisibility() == View.VISIBLE && !JDReadApplication.getInstance().getLogin()) {
            ToastUtil.showToast(ResManager.getString(R.string.login_resutl_not_login));
            return;
        }
        binding.startWifiAdd.setVisibility(binding.startWelcome.startFirst.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        binding.startPreference.startPreference.setVisibility(binding.startLogin.startLogin.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        binding.startLogin.startLogin.setVisibility(binding.startWifiRecycler.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        binding.startWifiRecycler.setVisibility(binding.startWelcome.startFirst.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        binding.startWelcome.startFirst.setVisibility(View.GONE);
    }

    private void initCategory() {
        binding.startPreference.startPreferenceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.startPreference.startPreferenceRecycler.addItemDecoration(decoration);
        readPreferenceAdapter = new ReadPreferenceAdapter();
        binding.startPreference.startPreferenceRecycler.setAdapter(readPreferenceAdapter);
        binding.startPreference.startPreferenceRecycler.setPageTurningCycled(true);
    }

    private void initLogin() {
        UserLoginViewModel userLoginViewModel = StartBundle.getInstance().getPersonalViewModel().getUserLoginViewModel();
        userLoginViewModel.isShowPassword.set(false);
        binding.startLogin.setLoginViewModel(userLoginViewModel);
    }

    private void initWifi() {
        binding.startWifiRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.startWifiRecycler.addItemDecoration(dividerItemDecoration);
        wifiSettingAdapter = new WifiSettingAdapter();
        binding.startWifiRecycler.setAdapter(wifiSettingAdapter);
        initWifiAdmin();
        if (!Utils.isNetworkConnected(StartActivity.this)) {
            wifiAdmin.toggleWifi();
        }
    }

    private void initWifiAdmin() {
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

    private void updateUI(boolean isWifiEnable) {
        if (!isWifiEnable) {
            wifiSettingAdapter.getScanResult().clear();
            wifiSettingAdapter.notifyDataSetChanged();
        }
    }

    private void updateAccessPointDetailedState(NetworkInfo.DetailedState state) {
        if (wifiSettingAdapter.getScanResult() != null) {
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
        wifiConnectedDialog.show(getFragmentManager());
    }

    private void addWifi() {
        final AddWIFIConfigurationDialog.DialogModel dialogModel = new AddWIFIConfigurationDialog.DialogModel();
        AddWIFIConfigurationDialog.Builder builder = new AddWIFIConfigurationDialog.Builder(this, dialogModel);
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
                WifiConfiguration wifiConfiguration = wifiAdmin.createWifiConfiguration(dialogModel.ssid.get(), dialogModel.password.get(), dialogModel.type.get());
                wifiAdmin.addNetwork(wifiConfiguration);
                updateUI(true);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnectedEvent(NetworkConnectedEvent event) {
        EncryptHelper.getSaltValue(PersonalDataBundle.getInstance(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        if (ResManager.getString(R.string.login_success).equals(event.getMessage())) {
            JDReadApplication.getInstance().setLogin(true);
            clearInput();
            ToastUtil.showToast(ResManager.getString(R.string.login_success));
            next();
            getCategory();
        } else {
            ToastUtil.showToast(event.getMessage());
        }
        Utils.hideSoftWindow(this);
    }

    private void clearInput() {
        getUserLoginViewModel().cleanInput();
    }

    public UserLoginViewModel getUserLoginViewModel() {
        return StartBundle.getInstance().getPersonalViewModel().getUserLoginViewModel();
    }

    private void getCategory() {
        final BookCategoryAction action = new BookCategoryAction();
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> cateTwo = ShopDataBundle.getInstance().getCategoryBean();
                if (readPreferenceAdapter != null) {
                    handleData(cateTwo);
                }
            }
        });
    }

    private void handleData(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categorys) {
        for (int i = 0; i < categorys.size(); i++) {
            CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo category = categorys.get(i);
            category.name = changeCategoryName(category.name);
            if (!ResManager.getString(R.string.category_publish).equals(category.name)) {
                continue;
            }
            List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> subCategory = categorys.get(i).sub_category;
            readPreferenceAdapter.setData(subCategory);
        }
    }

    private String changeCategoryName(String name) {
        String result = "";
        if (Constants.CATEGORY_MATH_CONTENT.equals(name)) {
            result = ResManager.getString(R.string.category_publish);
        } else if (Constants.CATEGORY_BOY_ORIGINAL.equals(name)) {
            result = ResManager.getString(R.string.category_boy);
        } else if (Constants.CATEGORY_GIRL_ORIGINAL.equals(name)) {
            result = ResManager.getString(R.string.category_girl);
        } else {
            result = name;
        }
        return result;
    }

    private void savePreference() {
        final List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> selectedBean = readPreferenceAdapter.getSelectedBean();
        if (selectedBean != null) {
            final SetReadPreferenceAction action = new SetReadPreferenceAction(selectedBean);
            action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    SetReadPreferenceBean resultBean = action.getResultBean();
                    if (resultBean.getResultCode() == 0) {
                        finishGuide();
                    }
                }
            });
        }
    }

    private void finishGuide() {
        ManagerActivityUtils.startPreloadActivity(StartActivity.this);
        JDPreferenceManager.setStringValue(Constants.IS_GUIDE, Constants.IS_GUIDE);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestFailedEvent(RequestFailedEvent event) {
        ToastUtil.showToast(event.getMessage());
    }
}
