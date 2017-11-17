package com.onyx.knote.ui.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.knote.R;

public class DialogNoteNameInput extends OnyxAlertDialog {
    static final public String ARGS_TITTLE = "args_tittle";
    static final public String ARGS_HINT = "args_hint";
    static final public String ARGS_ENABLE_NEUTRAL_OPTION = "args_enable_neutral_option";

    EditText mInputEditText = null;

    public void setCallBack(ActionCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    ActionCallBack mCallBack = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public interface ActionCallBack {
        boolean onConfirmAction(String input);

        void onCancelAction();

        void onDiscardAction();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String dialog_tittle = getArguments().getString(ARGS_TITTLE);
        final String dialog_hint = getArguments().getString(ARGS_HINT);
        final boolean enableNeutralOption = getArguments().getBoolean(ARGS_ENABLE_NEUTRAL_OPTION, false);
        Params params = new Params().setTittleString(dialog_tittle)
                .setEnableNeutralButton(enableNeutralOption)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_new_note)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mInputEditText = (EditText) customView.findViewById(R.id.editText_Input);
                        mInputEditText.setHint(dialog_hint);
                        mInputEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.length() >= 20) {
                                    mInputEditText.setError(getString(R.string.name_length_illegal));
                                } else {
                                    mInputEditText.setError(null);
                                }
                            }
                        });
                    }
                })
                .setNeutralButtonText(getString(R.string.discard))
                .setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onDiscardAction();
                        }
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            if (mInputEditText.getText().toString().trim().length() == 0 &&
                                    (mInputEditText.getHint() == null || mInputEditText.getHint().toString().trim().length() == 0)) {
                                Toast.makeText(getActivity(), R.string.name_can_not_empty, Toast.LENGTH_LONG).show();
                                return;
                            }
                            String inputString = mInputEditText.getText().toString().trim().length() == 0 ?
                                    mInputEditText.getHint().toString() : mInputEditText.getText().toString().trim();
                            if (mCallBack.onConfirmAction(inputString)) {
                                dismiss();
                            }
                        }
                    }
                }).setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onCancelAction();
                        }
                    }
                });

        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogNoteNameInput.class.getSimpleName());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCallBack != null) {
            mCallBack.onCancelAction();
        }
    }

}
