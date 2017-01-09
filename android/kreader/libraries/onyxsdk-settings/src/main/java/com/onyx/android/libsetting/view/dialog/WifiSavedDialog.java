package com.onyx.android.libsetting.view.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

import com.onyx.android.libsetting.R;

import static com.onyx.android.libsetting.util.Constant.ARGS_SECURITY_MODE;
import static com.onyx.android.libsetting.util.Constant.ARGS_SIGNAL_LEVEL;
import static com.onyx.android.libsetting.util.Constant.ARGS_SSID;

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
        final String ssid = arguments.getString(ARGS_SSID);
        final String signalLevel = arguments.getString(ARGS_SIGNAL_LEVEL);
        final String securityMode = arguments.getString(ARGS_SECURITY_MODE);
        Params params = new Params().setTittleString(ssid)
                .setCustomContentLayoutResID(R.layout.alert_dialog_wifi_saved)
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
