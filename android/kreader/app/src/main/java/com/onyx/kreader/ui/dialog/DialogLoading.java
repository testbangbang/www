package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.onyx.kreader.R;

public class DialogLoading extends DialogBase
{

    private static final String TAG = DialogLoading.class.getSimpleName();

    private TextView mTextViewMessage = null;
    private ImageView mCancelButton=null;

    public DialogLoading(Context context, String msg, boolean enableCancel)
    {
        super(context,  R.style.dialog_progress);

        setContentView(R.layout.dialog_loading);

        mTextViewMessage = (TextView) findViewById(R.id.textview_message);
        mTextViewMessage.setText(msg);
        mCancelButton=(ImageView)findViewById(R.id.button_cancel);
        if (!enableCancel) {
            findViewById(R.id.divider_line).setVisibility(View.INVISIBLE);
            mCancelButton.setVisibility(View.INVISIBLE);
        }
        setCanceledOnTouchOutside(false);
    }



    public void setMessage(String msg)
    {
        if (!mTextViewMessage.getText().equals(msg)) {
            mTextViewMessage.setText(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.cancel();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setCancelButtonClickListener(Button.OnClickListener mCancelButtonOnClickListener){
        mCancelButton.setOnClickListener(mCancelButtonOnClickListener);
    }
}
