package com.onyx.edu.manager.view.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.edu.manager.R;

/**
 * Created by suicheng on 2017/6/15.
 */
public class SideBar extends View {

    public static String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private int choose = -1;
    private Paint paint = new Paint();

    private TextView textDialog;

    private Context context;

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

    public void setTextView(TextView mTextDialog) {
        this.textDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SideBar(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letter.length;

        for (int i = 0; i < letter.length; i++) {
            paint.setColor(getResources().getColor(R.color.side_bar_text));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(ScreenUtils.getDimenPixelSize(context, 12));
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(letter[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letter[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * letter.length);
        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (textDialog != null) {
                    textDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < letter.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letter[c]);
                        }
                        if (textDialog != null) {
                            textDialog.setText(letter[c]);
                            textDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener listener) {
        this.onTouchingLetterChangedListener = listener;
    }
}
