package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.OnyxProgressBar;

/**
 * @author qingyue
 */
public class DialogReaderLoading extends OnyxBaseDialog {
    private static final String TAG = DialogReaderLoading.class.getSimpleName();

    private PowerManager.WakeLock mWakeLock = null;
    private OnyxProgressBar mProgressBar = null;
    private RelativeLayout mDialogLoadingLayout = null;

    public interface onFinishReaderListener {
        void onFinishReader();
    }

    private onFinishReaderListener mOnFinishReaderListener = new onFinishReaderListener() {
        @Override
        public void onFinishReader() {
        }
    };

    public void SetOnFinishReaderListener(onFinishReaderListener l) {
        mOnFinishReaderListener = l;
    }

    private TextView mTextViewMessage = null;

    public DialogReaderLoading(Context context, String msg) {
        super(context, R.style.dialog_progress);
        setContentView(R.layout.dialog_reader_loading);

        mDialogLoadingLayout = (RelativeLayout) findViewById(R.id.dialog_loading_layout);
        mProgressBar = (OnyxProgressBar) findViewById(R.id.imageview_loading);
        mTextViewMessage = (TextView) findViewById(R.id.textview_message);
        mTextViewMessage.setText(msg);
        setCanceledOnTouchOutside(false);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (mWakeLock == null) {
                    mWakeLock = Device.currentDevice.newWakeLock(getContext(), TAG);
                    mWakeLock.acquire();
                } else {
                    mWakeLock.acquire();
                }
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mWakeLock != null) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
//                EpdController.invalidate(DialogLoading.this.getWindow().getDecorView(), UpdateMode.GC);
            }
        });
    }

    public void setProgressAnimResource(int id) {
        mProgressBar.setAnimResource(id);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(lp);
        mDialogLoadingLayout.setBackgroundDrawable(null);
    }

    public void setMessage(String msg) {
        if (!mTextViewMessage.getText().equals(msg)) {
            mTextViewMessage.setText(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.cancel();
            mOnFinishReaderListener.onFinishReader();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
