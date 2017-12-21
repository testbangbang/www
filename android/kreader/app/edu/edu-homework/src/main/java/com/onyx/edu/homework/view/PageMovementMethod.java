package com.onyx.edu.homework.view;

import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by lxm on 2017/12/20.
 */

public class PageMovementMethod extends ScrollingMovementMethod {

    @Override
    public boolean pageUp(TextView widget, Spannable buffer) {
        return super.pageUp(widget, buffer);
    }

    @Override
    public boolean pageDown(TextView widget, Spannable buffer) {
        return super.pageDown(widget, buffer);
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return true;
    }
}
