package com.onyx.android.libsetting.view.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.libsetting.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

import static com.onyx.android.libsetting.util.Constant.ARGS_SECURITY_MODE;
import static com.onyx.android.libsetting.util.Constant.ARGS_SIGNAL_LEVEL;
import static com.onyx.android.libsetting.util.Constant.ARGS_SSID;

/**
 * Created by solskjaer49 on 2016/12/2 16:06.
 */

public class WifiLoginDialog extends OnyxAlertDialog {
    static final String TAG = WifiLoginDialog.class.getSimpleName();
    EditText editTextPassword;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onConnectToAccessPoint(String password);
    }

    private Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String ssid = arguments.getString(ARGS_SSID);
        final String signalLevel = arguments.getString(ARGS_SIGNAL_LEVEL);
        final String securityMode = arguments.getString(ARGS_SECURITY_MODE);
        Params params = new Params().setTittleString(ssid)
                .setCustomContentLayoutResID(R.layout.alert_dialog_wifi_login)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        getPositiveButton().setEnabled(false);
                        TextView tvSignalLevel = (TextView) customView.findViewById(R.id.text_view_signal_level);
                        TextView tvSecurity = (TextView) customView.findViewById(R.id.text_view_security);
                        editTextPassword = (EditText) customView.findViewById(R.id.edit_text_password);
                        CheckBox showPasswordCheck = (CheckBox) customView.findViewById(R.id.cb_show_password);
                        tvSignalLevel.setText(signalLevel);
                        tvSecurity.setText(securityMode);
                        showPasswordCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                int cursor = editTextPassword.getSelectionStart();
                                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | (
                                        isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                                : InputType.TYPE_TEXT_VARIATION_PASSWORD));
                                editTextPassword.setSelection(cursor);
                            }
                        });
                        editTextPassword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (editTextPassword.getText().length() >= 5) {
                                    getPositiveButton().setEnabled(true);
                                } else {
                                    getPositiveButton().setEnabled(false);
                                }
                            }
                        });
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onConnectToAccessPoint(editTextPassword.getText().toString());
                        }
                        dismiss();
                    }
                }).setPositiveButtonText(getString(R.string.wifi_connect));
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
