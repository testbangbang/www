package com.onyx.jdread.setting.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.annotations.NonNull;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;

/**
 * Created by suicheng on 2018/2/7.
 */
public class NumberKeyboardPopWindow extends PopupWindow {
    private TextView[] textViews;
    private View contentView;
    private NumberKeyboardView keyboardView;
    private EditText editText;
    private StringBuilder sb = new StringBuilder();

    public NumberKeyboardPopWindow(@NonNull Context context, @NonNull EditText editText, NumberKeyboardView.OnKeyboardListener listener) {
        super(context);
        init(context);
        bindEdit(editText, listener);
    }

    public NumberKeyboardPopWindow(@NonNull Context context, NumberKeyboardView.OnKeyboardListener listener, @NonNull TextView[] textViews) {
        super(context);
        init(context);
        bindTextViews(textViews, listener);
    }

    private void init(@NonNull Context context) {
        contentView = LayoutInflater.from(context).inflate(R.layout.pop_number_keyborad_layout, null);
        keyboardView = (NumberKeyboardView) contentView.findViewById(R.id.keyboard_view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(contentView);
        setOutsideTouchable(false);
        setClippingEnabled(true);
    }

    public void showAtBottomCenter(View parent) {
        showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void bindTextViews(final TextView[] textViews, final NumberKeyboardView.OnKeyboardListener listener) {
        this.textViews = textViews;
        final int length = textViews.length;
        keyboardView.setKeyboardListener(new NumberKeyboardView.OnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                for (int i = 0; i < length; i++) {
                    TextView textView = textViews[i];
                    String s = textView.getText().toString();
                    if (StringUtils.isNullOrEmpty(s)) {
                        textView.setText(text);
                        sb.append(text);
                        if (i == length - 1 && listener != null) {
                            listener.onFinishEvent(sb.toString());
                        }
                        break;
                    }
                }
                if (listener != null) {
                    listener.onInsertKeyEvent(text);
                }
            }

            @Override
            public void onDeleteKeyEvent() {
                for (int i = length - 1; i >= 0; i--) {
                    TextView textView = textViews[i];
                    String s = textView.getText().toString();
                    if (StringUtils.isNotBlank(s)) {
                        textView.setText("");
                        sb.deleteCharAt(sb.length() - 1);
                        break;
                    }
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

            @Override
            public void onFinishEvent(String password) {

            }
        });
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

            @Override
            public void onFinishEvent(String password) {

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

    public void setTextViews(TextView[] textViews) {
        this.textViews = textViews;
    }
}
