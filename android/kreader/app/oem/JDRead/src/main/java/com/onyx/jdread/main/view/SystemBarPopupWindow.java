package com.onyx.jdread.main.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
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

import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SystemBarPopLayoutBinding;
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
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(bind.getRoot());
        initEvent(systemBarPopupModel);
    }

    private void initEvent(final SystemBarPopupModel systemBarPopupModel) {
        bind.ratingbarLightSettings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (systemBarPopupModel != null) {
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
        public final ObservableInt wifiRes = new ObservableInt();

        public SystemBarPopupModel() {
            updateWifi();
        }

        public void updateWifi() {
            if (NetworkUtil.isWifiEnabled(JDReadApplication.getInstance())) {
                WifiManager wifiManager = (WifiManager) JDReadApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentWifi.set(wifiInfo.getSSID());
                wifiRes.set(R.drawable.ic_top_menu_wifi);
            } else {
                currentWifi.set("WLAN");
                wifiRes.set(R.drawable.ic_wifi_90px);
            }
        }

        public void toggleWifi() {
            wifiRes.set(NetworkUtil.isWifiEnabled(JDReadApplication.getInstance()) ? R.drawable.ic_wifi_90px : R.drawable.ic_top_menu_wifi);
            NetworkUtil.toggleWiFi(JDReadApplication.getInstance());
        }

        public void toggleA2Model() {

        }

        public void openSetting() {
            EventBus.getDefault().post(new SystemBarBackToSettingEvent());
        }
    }
}
