package com.onyx.android.sdk.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.DimenUtils;

/**
 * Created by ming on 16/8/24.
 */
public class DialogHelp {

    public static Builder getDialog(Context context) {
        Builder builder = new Builder(context, R.style.base_dialog);
        return builder;
    }

    public static Builder getConfirmDialog(Context context,
                                           String message,
                                           DialogInterface.OnClickListener onOkClickListener,
                                           DialogInterface.OnClickListener onCancelClickListener) {
        Builder builder = getDialog(context);

        TextView titleView = getCustomTitle(context);
        titleView.setText(message);
        builder.setCustomTitle(titleView);
        builder.setPositiveButton(context.getString(R.string.ok), onOkClickListener);
        builder.setNegativeButton(context.getString(R.string.cancel), onCancelClickListener);
        builder.setCancelable(false);
        return builder;
    }

    public static Builder getInputDialog(Context context, String title, EditText editText, DialogInterface.OnClickListener onClickListener) {
        Builder builder = getDialog(context);

        TextView titleView = getCustomTitle(context);
        titleView.setText(title);
        builder.setCustomTitle(titleView);
        builder.setPositiveButton(context.getString(R.string.ok), onClickListener);
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.setView(editText);
        return builder;
    }

    private static TextView getCustomTitle(Context context) {
        TextView titleView = new TextView(context);
        int padding = (int) context.getResources().getDimension(R.dimen.alert_dialog_padding);
        titleView.setPadding(padding, padding, padding, padding);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(context.getResources().getDimension(R.dimen.alert_dialog_text_size));
        return titleView;
    }

    public static class Builder extends AlertDialog.Builder {

        public Builder(Context context) {
            super(context);
        }

        public Builder(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public AlertDialog create() {
            AlertDialog dialog = super.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_bg);
            return dialog;
        }
    }

}
