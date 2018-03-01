package com.onyx.jdread.setting.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * Created by li on 2018/2/27.
 */

public class SpaceEditText extends EditText {
    public SpaceEditText(Context context) {
        super(context);
    }

    public SpaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            CharSequence _text = getText();
            int min = 0;
            int max = _text.length();
            int length = _text.length();
            int _count = getMaxLength();

            if (isFocused()) {
                final int selStart = getSelectionStart();
                final int selEnd = getSelectionEnd();

                min = Math.max(0, Math.min(selStart, selEnd));
                max = Math.max(0, Math.max(selStart, selEnd));
            }
            ClipboardManager clipboard =
                    (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = clipboard.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                boolean didFirst = false;
                int _start = this.getSelectionStart();
                CharSequence paste = clip.getItemAt(0).getText();
                if (paste != null) {
                    if (!didFirst) {
                        Selection.setSelection((Spannable) _text, max);
                        ((Editable) _text).replace(min, max, paste);
                        didFirst = true;
                    } else {
                        ((Editable) _text).insert(getSelectionEnd(), paste);
                    }
                }
                this.setText(_text);
                if (length + paste.length() <= _count) {
                    this.setSelection(_start + paste.length());
                } else {
                    int insert = length + paste.length() - _count;
                    this.setSelection(_start + paste.length() - insert);
                }
            }
            return true;
        }
        return super.onTextContextMenuItem(id);
    }

    public int getMaxLength() {
        int length = 0;
        try {
            InputFilter[] inputFilters = getFilters();
            for (InputFilter filter : inputFilters) {
                Class<?> c = filter.getClass();
                if (c.getName().equals("android.text.InputFilter$LengthFilter")) {
                    Field[] f = c.getDeclaredFields();
                    for (Field field : f) {
                        if (field.getName().equals("mMax")) {
                            field.setAccessible(true);
                            length = (Integer) field.get(filter);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }
}
