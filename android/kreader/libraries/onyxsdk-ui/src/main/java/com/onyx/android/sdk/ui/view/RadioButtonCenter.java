package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by ming on 16/10/8.
 */

public class RadioButtonCenter extends AppCompatRadioButton {

    private Drawable buttonDrawable;

    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, 0, 0);
        buttonDrawable = a.getDrawable(0);
        setButtonDrawable(android.R.color.transparent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().height == LinearLayout.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = buttonDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        }
        if (getLayoutParams().width == LinearLayout.LayoutParams.WRAP_CONTENT) {
            widthMeasureSpec = buttonDrawable.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buttonDrawable != null) {
            buttonDrawable.setState(getDrawableState());
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();

            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }

            int buttonWidth = buttonDrawable.getIntrinsicWidth();
            int buttonLeft = (getWidth() - buttonWidth) / 2;

            if (buttonLeft % 2 != 0) {
                buttonLeft++;
            }

            if (y % 2 != 0) {
                y++;
            }

            buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
            buttonDrawable.draw(canvas);
        }
//        AppCompatUtils.processViewLayoutEvenPosition(this);
    }
}
