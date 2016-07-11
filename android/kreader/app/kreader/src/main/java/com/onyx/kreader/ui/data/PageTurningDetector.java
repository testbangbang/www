package com.onyx.kreader.ui.data;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Joy on 14-2-21.
 */
public abstract class PageTurningDetector {

    public static PageTurningDirection detectHorizontalTuring(Context context, int deltaX) {
        final int X_DELTA_THRESHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        if (Math.abs(deltaX) < X_DELTA_THRESHOLD) {
            return PageTurningDirection.None;
        }

        return deltaX > 0 ? PageTurningDirection.Left : PageTurningDirection.Right;
    }

    public static PageTurningDirection detectVerticalTuring(Context context, int deltaY) {
        final int Y_DELTA_THRESHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        if (Math.abs(deltaY) < Y_DELTA_THRESHOLD) {
            return PageTurningDirection.None;
        }

        return deltaY > 0 ? PageTurningDirection.Left : PageTurningDirection.Right;
    }

    public static PageTurningDirection detectBothAxisTuring(Context context, int deltaX, int deltaY) {
        final int X_DELTA_THRESHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        final int Y_DELTA_THRESHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        if (Math.abs(deltaX) < X_DELTA_THRESHOLD && Math.abs(deltaY) < Y_DELTA_THRESHOLD) {
            return PageTurningDirection.None;
        }

        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
            return deltaX > 0 ? PageTurningDirection.Left : PageTurningDirection.Right;
        } else {
            return deltaY > 0 ? PageTurningDirection.Left : PageTurningDirection.Right;
        }

    }
}
