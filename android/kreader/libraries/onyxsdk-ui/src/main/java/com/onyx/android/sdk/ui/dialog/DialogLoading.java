package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.EllipsizingTextView;

public class DialogLoading extends OnyxBaseDialog {
    private static final String TAG = DialogLoading.class.getSimpleName();

    private EllipsizingTextView mTextViewToastMessage, mTextViewProgressMessage;
    private ImageView mCancelButton = null;
    private ImageView mConfirmButton = null;
    private Button.OnClickListener clickListener = null;
    private boolean afterDoneOpen = false;

    public DialogLoading(Context context, String msg, boolean enableCancel) {
        super(context, R.style.dialog_progress);
        setContentView(R.layout.dialog_loading);
        mTextViewToastMessage = (EllipsizingTextView) findViewById(R.id.textView_toast_message);
        mTextViewProgressMessage = (EllipsizingTextView) findViewById(R.id.textView_progress_message);
        mTextViewToastMessage.setText(msg);
        mCancelButton = (ImageView) findViewById(R.id.button_cancel);
        mConfirmButton = (ImageView) findViewById(R.id.button_confirm);
        if (!enableCancel) {
            findViewById(R.id.divider_line).setVisibility(View.GONE);
            mCancelButton.setVisibility(View.GONE);
        }
        setToastViewMaxWidth(getPercentageWidth(context));
        setCanceledOnTouchOutside(false);
    }

    public DialogLoading(Context context, int msgResID, boolean enableCancel) {
        this(context, context.getResources().getString(msgResID), enableCancel);
    }

    public void setAfterDoneOpen(boolean afterDoneOpen) {
        this.afterDoneOpen = afterDoneOpen;
    }

    public boolean checkOpenOrNot(Button.OnClickListener confirmClickListener) {
        if (afterDoneOpen) {
            findViewById(R.id.divider_lineL).setVisibility(View.VISIBLE);
            mConfirmButton.setVisibility(View.VISIBLE);
            setConfirmButtonClickListener(confirmClickListener);
        }

        return afterDoneOpen;
    }

    public void setToastMessage(String msg) {
        if (!mTextViewToastMessage.getText().equals(msg)) {
            mTextViewToastMessage.setText(msg);
        }
    }

    public void setToastMessage(int msgResID) {
        setToastMessage(getContext().getResources().getString(msgResID));
    }

    public void setProgressMessage(String msg) {
        mTextViewProgressMessage.setVisibility(View.VISIBLE);
        mTextViewProgressMessage.setText(msg);
    }

    public void setProgressMessage(int msgResID) {
        setProgressMessage(getContext().getResources().getString(msgResID));
    }

    public void setProgressMessageInvisible() {
        mTextViewProgressMessage.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancel();
            if (clickListener != null) {
                clickListener.onClick(mCancelButton);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCancelButtonClickListener(Button.OnClickListener cancelClickListener) {
        clickListener = cancelClickListener;
        mCancelButton.setOnClickListener(cancelClickListener);
    }

    public void setConfirmButtonClickListener(Button.OnClickListener confirmClickListener) {
        clickListener = confirmClickListener;
        mConfirmButton.setOnClickListener(confirmClickListener);
    }

    public void setToastViewMaxWidth(int width) {
        mTextViewToastMessage.setMaxWidth(width);
    }

    private int getPercentageWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return (dm.widthPixels * 5 / 10);
    }
}
