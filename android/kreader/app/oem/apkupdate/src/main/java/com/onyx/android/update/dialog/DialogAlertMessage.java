package com.onyx.android.update.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.update.R;

/**
 * Created by suicheng on 2018/3/20.
 */
public class DialogAlertMessage extends AlertDialog {

    public DialogAlertMessage(@NonNull Context context) {
        super(context, R.style.CustomDialogStyle);
    }

    private View.OnClickListener positiveAction;
    private String message;

    public void setPositiveAction(View.OnClickListener listener) {
        positiveAction = listener;
    }

    public void setMessage(String text) {
        this.message = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alert_message, null);
        view.findViewById(R.id.button_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveAction != null) {
                    positiveAction.onClick(v);
                }
            }
        });
        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(String.valueOf(message));
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }
}
