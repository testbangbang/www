package com.onyx.android.dr.devicesetting.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.devicesetting.data.util.Constant;


/**
 * Created by solskjaer49 on 2016/12/2 16:06.
 */

public class WifiConnectedDialog extends OnyxAlertDialog {


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onForgetAccessPoint();
    }

    private Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String ssid = arguments.getString(Constant.ARGS_SSID);
        final String signalLevel = arguments.getString(Constant.ARGS_SIGNAL_LEVEL);
        final String securityMode = arguments.getString(Constant.ARGS_SECURITY_MODE);
        final String linkSpeed = arguments.getString(Constant.ARGS_LINK_SPEED);
        final String ipAddress = arguments.getString(Constant.ARGS_IP_ADDRESS);
        final String band = arguments.getString(Constant.ARGS_BAND);
        Params params = new Params().setTittleString(ssid)
                .setCustomContentLayoutResID(R.layout.alert_dialog_wifi_connected)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TextView tvSignalLevel = (TextView) customView.findViewById(R.id.text_view_signal_level);
                        TextView tvSecurity = (TextView) customView.findViewById(R.id.text_view_security);
                        TextView tvLinkSpeed = (TextView) customView.findViewById(R.id.text_view_link_speed);
                        TextView tvIpAddress = (TextView) customView.findViewById(R.id.text_view_ip_address);
                        TextView tvBand = (TextView) customView.findViewById(R.id.text_view_band);
                        tvSignalLevel.setText(signalLevel);
                        tvSecurity.setText(securityMode);
                        tvLinkSpeed.setText(linkSpeed);
                        tvBand.setText(band);
                        tvIpAddress.setText(ipAddress);
                        if (TextUtils.isEmpty(band)){
                            customView.findViewById(R.id.band_panel).setVisibility(View.GONE);
                        }
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onForgetAccessPoint();
                        }
                        dismiss();
                    }
                })
                .setPositiveButtonText(getString(R.string.wifi_forget));
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager manager) {
        super.show(manager, WifiConnectedDialog.class.getSimpleName());
    }
}
