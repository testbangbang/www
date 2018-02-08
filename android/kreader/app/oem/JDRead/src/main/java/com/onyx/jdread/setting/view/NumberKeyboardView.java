package com.onyx.jdread.setting.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.onyx.jdread.R;

import java.util.List;

/**
 * Created by suicheng on 2018/2/7.
 */

public class NumberKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    private static final int KEYCODE_CUSTOM = -10;

    private int mDeleteBackgroundColor;
    private Rect mDeleteDrawRect;
    private Drawable mDeleteDrawable;

    private String customText = "";
    private Drawable customDrawable;
    private Rect customDrawRect;
    private Paint customTextPaint;
    private float customTextY = -1;

    private OnKeyboardListener keyboardListener;

    public NumberKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NumberKeyboardView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardView, defStyleAttr, 0);
        mDeleteDrawable = a.getDrawable(R.styleable.NumberKeyboardView_delete_drawable);
        mDeleteBackgroundColor = a.getColor(R.styleable.NumberKeyboardView_delete_backgroundColor, Color.TRANSPARENT);
        checkCustomViewType(a);
        a.recycle();

        Keyboard keyboard = new Keyboard(context, R.xml.keyboard_number_layout);
        setKeyboard(keyboard);

        setEnabled(true);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(this);
    }

    private void checkCustomViewType(TypedArray a) {
        customText = a.getString(R.styleable.NumberKeyboardView_custom_string);
        customDrawable = a.getDrawable(R.styleable.NumberKeyboardView_custom_drawable);
        customTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        customTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.keyboard_custom_text_size));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == KEYCODE_CUSTOM) {
                drawKeyBackground(key, canvas, mDeleteBackgroundColor);
                drawCustomView(canvas, key, mDeleteBackgroundColor);
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawKeyBackground(key, canvas, mDeleteBackgroundColor);
                drawDeleteButton(canvas, key);
            }
        }
    }

    private void drawCustomView(Canvas canvas, Keyboard.Key key, int color) {
        drawKeyBackground(key, canvas, color);
        if (customDrawable != null) {
            drawCustomButton(canvas, key);
        } else {
            drawCustomText(canvas, key);
        }
    }

    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, int color) {
        ColorDrawable drawable = new ColorDrawable(color);
        drawable.setBounds(getKeyRect(key));
        drawable.draw(canvas);
    }

    private Rect getKeyDrawableRect(Keyboard.Key key, Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int drawWidth = intrinsicWidth;
        int drawHeight = intrinsicHeight;

        if (drawWidth > key.width) {
            drawWidth = key.width;
            drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
        }
        if (drawHeight > key.height) {
            drawHeight = key.height;
            drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
        }

        int left = key.x + (key.width - drawWidth) / 2;
        int top = key.y + (key.height - drawHeight) / 2;
        return new Rect(left, top, left + drawWidth, top + drawHeight);
    }

    private Rect getKeyRect(Keyboard.Key key) {
        return new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
    }

    private void drawCustomText(Canvas canvas, Keyboard.Key key) {
        Rect rect = getKeyRect(key);
        if (customTextY < 0) {
            Paint.FontMetrics fontMetrics = customTextPaint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            customTextY = (rect.centerY() - top / 2 - bottom / 2);
        }
        canvas.drawText(customText, rect.centerX(), customTextY, customTextPaint);
    }

    private void drawCustomButton(Canvas canvas, Keyboard.Key key) {
        if (customDrawRect == null || customDrawRect.isEmpty()) {
            customDrawRect = getKeyDrawableRect(key, customDrawable);
        }
        drawCommonButton(canvas, customDrawable, customDrawRect);
    }

    private void drawDeleteButton(Canvas canvas, Keyboard.Key key) {
        if (mDeleteDrawRect == null || mDeleteDrawRect.isEmpty()) {
            mDeleteDrawRect = getKeyDrawableRect(key, mDeleteDrawable);
        }
        drawCommonButton(canvas, mDeleteDrawable, mDeleteDrawRect);
    }

    private void drawCommonButton(Canvas canvas, Drawable drawable, Rect rect) {
        drawable.setBounds(rect.left, rect.top, rect.right, rect.bottom);
        drawable.draw(canvas);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (keyboardListener != null) {
                keyboardListener.onDeleteKeyEvent();
            }
        } else if (primaryCode == KEYCODE_CUSTOM) {
            if (keyboardListener != null) {
                keyboardListener.onCustomKeyEvent();
            }
        } else {
            if (keyboardListener != null) {
                keyboardListener.onInsertKeyEvent(Character.toString((char) primaryCode));
            }
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    public void setCustomDrawable(Drawable drawable) {
        this.customDrawable = drawable;
        this.customText = null;
        invalidate();
    }

    public void setCustomText(String text) {
        this.customDrawable = null;
        this.customText = text;
        invalidate();
    }

    public void setKeyboardListener(OnKeyboardListener listener) {
        this.keyboardListener = listener;
    }

    public OnKeyboardListener getOnKeyboardListener() {
        return this.keyboardListener;
    }

    public interface OnKeyboardListener {

        void onInsertKeyEvent(String text);

        void onDeleteKeyEvent();

        void onCustomKeyEvent();
    }
}
