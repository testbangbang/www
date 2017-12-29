package com.onyx.jdread.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.jdread.databinding.DialogLoadingBinding;

public class LoadingDialog extends OnyxBaseDialog {
    private static final String TAG = LoadingDialog.class.getSimpleName();
    private DialogLoadingBinding dialogLoadingBinding;

    public LoadingDialog(Context context, String msg, boolean enableCancel) {
        super(context, R.style.dialog_progress);
        dialogLoadingBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_loading, (ViewGroup) getWindow().getDecorView().getRootView(), true);
        setToastViewMaxWidth(getPercentageWidth(context));
        setCanceledOnTouchOutside(enableCancel);
        if (msg != null) {
            setToastMessage(msg);
        }
    }

    public LoadingDialog(Context context, int msgResID, boolean enableCancel) {
        this(context, context.getResources().getString(msgResID), enableCancel);
    }

    public LoadingDialog(Context context, boolean enableCancel) {
        this(context, null, enableCancel);
    }

    public void setToastMessage(String msg) {
        dialogLoadingBinding.dialogMessage.setText(msg);
    }

    public void setToastMessage(int msgResID) {
        setToastMessage(getContext().getResources().getString(msgResID));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setToastViewMaxWidth(int width) {
        dialogLoadingBinding.dialogMessage.setMaxWidth(width);
    }

    private int getPercentageWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return (dm.widthPixels * 5 / 10);
    }
}