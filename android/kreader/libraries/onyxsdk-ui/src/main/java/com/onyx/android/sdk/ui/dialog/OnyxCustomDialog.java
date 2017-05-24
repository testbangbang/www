package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 2017/3/14.
 */

public class OnyxCustomDialog extends OnyxBaseDialog implements DialogInterface{

    private TextView title;
    private Button btnCancel;
    private Button btnOk;
    private EditText inputEditText;
    private SeekBar progressBar;

    public static OnyxCustomDialog getConfirmDialog(Context context, String message,
                                                    DialogInterface.OnClickListener onClickListener,
                                                    DialogInterface.OnClickListener onCancelListener) {
        return getConfirmDialog(context, message, true, onClickListener, onCancelListener);
    }

    public static OnyxCustomDialog getConfirmDialog(Context context, String message, boolean showNegativeButton,
                                                    DialogInterface.OnClickListener onClickListener,
                                                    DialogInterface.OnClickListener onCancelListener) {
        OnyxCustomDialog dialog = new OnyxCustomDialog(context);

        dialog.setTitle(message);
        dialog.setPositiveButton(context.getString(R.string.ok), onClickListener);
        dialog.setNegativeButton(context.getString(R.string.cancel), onCancelListener);
        if (!showNegativeButton) {
            dialog.btnCancel.setVisibility(View.GONE);
        }
        dialog.setCancelable(false);
        return dialog;
    }

    public static OnyxCustomDialog getInputDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        OnyxCustomDialog dialog = new OnyxCustomDialog(context);

        dialog.enableInputEditText();
        dialog.setTitle(message);
        dialog.setPositiveButton(context.getString(R.string.ok), onClickListener);
        dialog.setNegativeButton(context.getString(R.string.cancel), null);
        return dialog;
    }

    public OnyxCustomDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_custom);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);
        inputEditText = (EditText) findViewById(R.id.input);
        progressBar = (SeekBar) findViewById(R.id.seek_bar_view);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTitle(CharSequence message) {
        title.setText(message);
    }

    public void setTitle(int resId) {
        title.setText(resId);
    }

    public void setPositiveButton(CharSequence text, final OnClickListener listener) {
        btnOk.setText(text);
        if (listener != null) {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(OnyxCustomDialog.this, BUTTON_POSITIVE);
                    dismiss();
                }
            });
        }

    }

    public void setNegativeButton(CharSequence text, final OnClickListener listener) {
        btnCancel.setText(text);
        if (listener != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(OnyxCustomDialog.this, BUTTON_NEGATIVE);
                    dismiss();
                }
            });
        }
    }

    public void setCancelable(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
    }


    public EditText getInputEditText() {
        return inputEditText;
    }

    public Editable getInputValue() {
        return inputEditText.getText();
    }

    public void enableInputEditText() {
        inputEditText.setVisibility(View.VISIBLE);
    }

    public void enableProgress(final int max, final int progress, final SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(max);
        progressBar.setProgress(progress);
        progressBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }
}
