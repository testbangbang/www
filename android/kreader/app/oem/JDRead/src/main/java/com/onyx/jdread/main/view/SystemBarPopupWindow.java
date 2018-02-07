package com.onyx.jdread.main.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SystemBarPopLayoutBinding;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.SystemBarBackToSettingEvent;
import com.onyx.jdread.setting.model.BrightnessModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-24.
 */

public class SystemBarPopupWindow extends PopupWindow {
    private SystemBarPopupModel systemBarPopupModel;
    private SystemBarPopLayoutBinding bind;

    public SystemBarPopupWindow(@NonNull Context context, @NonNull SystemBarPopupModel systemBarPopupModel) {
        super(context);
        bind = DataBindingUtil.bind(View.inflate(context, R.layout.system_bar_pop_layout, null));
        bind.setSystemBarPopModel(systemBarPopupModel);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        bind.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SystemBarBackToSettingEvent());
                dismiss();
            }
        });
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(bind.getRoot());
        initEvent(systemBarPopupModel);
    }

    private void initEvent(final SystemBarPopupModel systemBarPopupModel) {
        bind.ratingbarLightSettings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    systemBarPopupModel.brightnessModel.setBrightness(ratingBar.getProgress());
                }
            }
        });
    }

    public void show(View parentView) {
        if (!isShowing()) {
            showAsDropDown(parentView);
        }
    }

    public static class SystemBarPopupModel extends Observable {
        public final BrightnessModel brightnessModel = new BrightnessModel();
        public final ObservableField<String> currentWifi = new ObservableField<>();
        public final ObservableBoolean wifiIsOn = new ObservableBoolean();
        public final ObservableBoolean speedRefresh = new ObservableBoolean();

        public SystemBarPopupModel() {
            updateWifi();
            speedRefresh.set(!EpdController.inSystemFastMode());
        }

        public void updateWifi() {
            if (NetworkUtil.isWifiEnabled(JDReadApplication.getInstance()) && NetworkUtil.isWiFiConnected(JDReadApplication.getInstance())) {
                WifiManager wifiManager = (WifiManager) JDReadApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentWifi.set(wifiInfo.getSSID());
            } else {
                currentWifi.set("WLAN");
            }
            wifiIsOn.set(NetworkUtil.isWifiEnabled(JDReadApplication.getInstance()));
        }

        public void toggleWifi() {
            wifiIsOn.set(!NetworkUtil.isWifiEnabled(JDReadApplication.getInstance()));
            NetworkUtil.toggleWiFi(JDReadApplication.getInstance());
        }

        public void toggleA2Model() {
            boolean useFastMode = !EpdController.inSystemFastMode();
            ToastUtil.showToast(useFastMode ? ResManager.getString(R.string.speed_refresh_is_opened) : ResManager.getString(R.string.speed_refresh_is_closed));
            speedRefresh.set(useFastMode);
            JDPreferenceManager.setBooleanValue(R.string.speed_refresh_key, useFastMode);
        }
    }
}
