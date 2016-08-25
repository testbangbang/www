package com.onyx.android.sdk.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 16/8/24.
 */
public class DialogHelp {

    public static Builder getDialog(Context context) {
        Builder builder = new Builder(context);
        return builder;
    }

    public static Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(context.getString(R.string.ok), onClickListener);
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.setCancelable(false);
        return builder;
    }

    public static class Builder extends AlertDialog.Builder{

        public Builder(Context context) {
            super(context);
        }

        public Builder(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public AlertDialog show() {
            AlertDialog dialog = super.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_bg);
            dialog.getWindow().setLayout((int)getContext().getResources().getDimension(R.dimen.alert_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            return dialog;
        }

    }
}
