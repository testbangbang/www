package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * Created by ming on 16/10/12.
 */

public class OnyxRadioButton extends AppCompatRadioButton {

    public OnyxRadioButton(Context context) {
        super(context);
    }

    public OnyxRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnyxRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static OnyxRadioButton Create(Context context, String text) {
        OnyxRadioButton radioButton = new OnyxRadioButton(context);
        radioButton.setText(text);
        return radioButton;
    }
}
