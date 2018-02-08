package com.onyx.jdread.util;

import android.os.Build;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * Created by suicheng on 2018/2/7.
 */

public class ViewCompatUtil {

    public static void disableEditShowSoftInput(EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod("setShowSoftInputOnFocus", new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception ignored) {
            }
        }
    }

    public static void disableEditShowSoftInput(EditText... editTexts) {
        if (editTexts == null || editTexts.length <= 0) {
            return;
        }
        for (EditText edit : editTexts) {
            disableEditShowSoftInput(edit);
        }
    }
}
