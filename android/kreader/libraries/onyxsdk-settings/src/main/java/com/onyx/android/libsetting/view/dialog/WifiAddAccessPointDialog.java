package com.onyx.android.libsetting.view.dialog;

import android.app.FragmentManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.libsetting.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

/**
 * Created by solskjaer49 on 2016/12/2 16:06.
 */

public class WifiAddAccessPointDialog extends OnyxAlertDialog {
    static final String TAG = WifiAddAccessPointDialog.class.getSimpleName();
    private EditText editTextPassword, editTextSSID;
    private TypedArray wifiSecuritySelectedValues;
    private int targetSecurityValue;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onConnectToNewConfiguration(String ssid, String password, int securityType);
    }

    private Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        wifiSecuritySelectedValues = getResources().obtainTypedArray(R.array.wifi_add_access_point_security_values);
        Params params = new Params().setTittleString(getString(R.string.add_network))
                .setCustomContentLayoutResID(R.layout.alert_dialog_wifi_add_access_point)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        getPositiveButton().setEnabled(false);
                        final LinearLayout securityInfoInputLayout = (LinearLayout) customView.findViewById(R.id.wifi_security_input_area);
                        securityInfoInputLayout.setVisibility(View.GONE);
                        editTextSSID = (EditText) customView.findViewById(R.id.edit_text_ssid);
                        editTextPassword = (EditText) customView.findViewById(R.id.edit_text_password);
                        Spinner securitySpinner = (Spinner) customView.findViewById(R.id.spinner_view_security);
                        securitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                targetSecurityValue = wifiSecuritySelectedValues.getInt(position, -1);
                                securityInfoInputLayout.setVisibility(targetSecurityValue > 0 ? View.VISIBLE : View.GONE);
                                getPositiveButton().setEnabled(
                                        isPositiveButtonLegal(editTextSSID.getText().toString(), editTextPassword.getText().toString()));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        CheckBox showPasswordCheck = (CheckBox) customView.findViewById(R.id.cb_show_password);
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
                        editTextSSID.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                getPositiveButton().setEnabled(
                                        isPositiveButtonLegal(editTextSSID.getText().toString(), editTextPassword.getText().toString()));
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
                                getPositiveButton().setEnabled(
                                        isPositiveButtonLegal(editTextSSID.getText().toString(), editTextPassword.getText().toString()));
                            }
                        });
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onConnectToNewConfiguration(editTextSSID.getText().toString(),
                                    targetSecurityValue > 0 ? editTextPassword.getText().toString() : "", targetSecurityValue);
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

    private boolean isPositiveButtonLegal(String ssid, String password) {
        if (targetSecurityValue <= 0) {
            return ssid.length() > 0;
        } else {
            return ssid.length() > 0 && password.length() > 5;
        }
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
