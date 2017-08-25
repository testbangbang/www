package com.onyx.android.dr.devicesetting.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.devicesetting.data.util.Constant;


/**
 * Created by solskjaer49 on 2016/12/2 16:06.
 */

public class WifiSavedDialog extends OnyxAlertDialog {
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onForgetAccessPoint();

        void onConnectToAccessPoint();
    }

    private Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String ssid = arguments.getString(Constant.ARGS_SSID);
        final String signalLevel = arguments.getString(Constant.ARGS_SIGNAL_LEVEL);
        final String securityMode = arguments.getString(Constant.ARGS_SECURITY_MODE);
        Params params = new Params().setTittleString(ssid)
                .setCustomContentLayoutResID(R.layout.alert_dialog_wifi_connected)
                .setEnableNeutralButton(true)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TextView tvSignalLevel = (TextView) customView.findViewById(R.id.text_view_signal_level);
                        TextView tvSecurity = (TextView) customView.findViewById(R.id.text_view_security);
                        tvSignalLevel.setText(signalLevel);
                        tvSecurity.setText(securityMode);
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
                .setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onConnectToAccessPoint();
                        }
                        dismiss();
                    }
                })
                .setNeutralButtonText(getString(R.string.wifi_connect))
                .setPositiveButtonText(getString(R.string.wifi_forget));
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager manager) {
        super.show(manager, WifiSavedDialog.class.getSimpleName());
    }
}
