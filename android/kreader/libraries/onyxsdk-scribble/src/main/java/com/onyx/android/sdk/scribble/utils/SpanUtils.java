package com.onyx.android.sdk.scribble.utils;

import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by lxm on 2017/8/30.
 */

public class SpanUtils {

    public static int calculateSpanTextFontHeight(LinedEditText spanTextView, int margin) {
        float bottom = spanTextView.getPaint().getFontMetrics().bottom;
        float top = spanTextView.getPaint().getFontMetrics().top;
        return (int) Math.ceil(bottom - top - 2 * margin);
    }

}
