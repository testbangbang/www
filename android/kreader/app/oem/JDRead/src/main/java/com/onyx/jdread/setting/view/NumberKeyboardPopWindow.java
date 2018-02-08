package com.onyx.jdread.setting.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.android.annotations.NonNull;
import com.onyx.jdread.R;

/**
 * Created by suicheng on 2018/2/7.
 */
public class NumberKeyboardPopWindow extends PopupWindow {

    private View contentView;
    private NumberKeyboardView keyboardView;
    private EditText editText;

    public NumberKeyboardPopWindow(@NonNull Context context, @NonNull EditText editText, NumberKeyboardView.OnKeyboardListener listener) {
        super(context);
        contentView = LayoutInflater.from(context).inflate(R.layout.pop_number_keyborad_layout, null);
        keyboardView = (NumberKeyboardView) contentView.findViewById(R.id.keyboard_view);
        this.editText = editText;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(contentView);
        setOutsideTouchable(false);
        setClippingEnabled(true);
        bindEdit(editText, listener);
    }

    public void showAtBottomCenter(View parent) {
        showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void bindEdit(final EditText editText, final NumberKeyboardView.OnKeyboardListener listener) {
        this.editText = editText;
        keyboardView.setKeyboardListener(new NumberKeyboardView.OnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                editText.getText().append(text);
                if (listener != null) {
                    listener.onInsertKeyEvent(text);
                }
            }

            @Override
            public void onDeleteKeyEvent() {
                int start = editText.length() - 1;
                if (start >= 0) {
                    editText.getText().delete(start, start + 1);
                }
                if (listener != null) {
                    listener.onDeleteKeyEvent();
                }
            }

            @Override
            public void onCustomKeyEvent() {
                if (listener != null) {
                    listener.onCustomKeyEvent();
                }
            }
        });
    }

    public NumberKeyboardView getKeyboardView() {
        return keyboardView;
    }

    public View getContentView() {
        return contentView;
    }

    public EditText getEditText() {
        return editText;
    }
}
