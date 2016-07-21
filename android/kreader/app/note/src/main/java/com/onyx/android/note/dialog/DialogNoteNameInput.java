package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

public class DialogNoteNameInput extends OnyxAlertDialog {
    static final public String ARGS_TITTLE = "args_tittle";
    static final public String ARGS_HINT = "args_hint";
    static final public String ARGS_ENABLE_NEUTRAL_OPTION = "args_enable_neutral_option";

    EditText mInputEditText = null;

    public void setCallBack(ActionCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    ActionCallBack mCallBack = null;

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
        setParams(new Params().setTittleString(dialog_tittle)
                .setEnableNeutralButton(enableNeutralOption)
                //TODO:hardcode mx style,if this app use on default branch,here could set layout id depend on config.
                .setCustomLayoutResID(R.layout.mx_custom_alert_dialog)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_new_note)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mInputEditText = (EditText) customView.findViewById(R.id.editText_Input);
                        mInputEditText.setHint(dialog_hint);
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
                }));
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
