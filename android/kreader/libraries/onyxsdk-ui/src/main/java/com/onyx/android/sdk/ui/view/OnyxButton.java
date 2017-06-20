package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by ming on 2017/1/11.
 */

public class OnyxButton extends Button{

    public OnyxButton(Context context) {
        super(context);
        init();
    }

    public OnyxButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnyxButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    setTextColor(Color.WHITE);
                }else {
                    setTextColor(Color.BLACK);
                }
            }
        });
    }
}
