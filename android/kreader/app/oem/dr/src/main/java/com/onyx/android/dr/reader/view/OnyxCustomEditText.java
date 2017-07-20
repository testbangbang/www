package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by ming on 16/8/20.
 */
public class OnyxCustomEditText extends EditText {

    public interface onKeyPreImeListener{
        void onKeyPreIme(int keyCode, KeyEvent event);
    }

    private onKeyPreImeListener onKeyPreImeListener;

    public OnyxCustomEditText(Context context) {
        super(context);
    }

    public OnyxCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnyxCustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnKeyPreImeListener(OnyxCustomEditText.onKeyPreImeListener onKeyPreImeListener) {
        this.onKeyPreImeListener = onKeyPreImeListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onKeyPreImeListener != null){
            onKeyPreImeListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
