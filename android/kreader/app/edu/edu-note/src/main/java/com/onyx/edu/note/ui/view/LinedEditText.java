package com.onyx.edu.note.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.onyx.edu.note.ui.SpanInputConnection;

/**
 * Created by zhuzeng on 8/18/16.
 */
public class LinedEditText extends AppCompatEditText {

    public interface InputConnectionListener {
        void commitText(CharSequence text, int newCursorPosition);
    }

    public interface OnKeyPreImeListener {
        void onKeyPreIme(int keyCode, KeyEvent event);
    }

    private Rect mRect;
    private Paint mPaint;
    private InputConnectionListener inputConnectionListener;
    private OnKeyPreImeListener onKeyPreImeListener;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
    }

    public void setInputConnectionListener(InputConnectionListener inputConnectionListener) {
        this.inputConnectionListener = inputConnectionListener;
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onKeyPreImeListener != null) {
            onKeyPreImeListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.actionLabel = null;
        outAttrs.label = "Test text";
        outAttrs.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new SpanInputConnection(this, true, new SpanInputConnection.Callback() {
            @Override
            public void commitText(CharSequence text, int newCursorPosition) {
                if (inputConnectionListener != null) {
                    inputConnectionListener.commitText(text, newCursorPosition);
                }
            }
        });
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        this.onKeyPreImeListener = onKeyPreImeListener;
    }
}