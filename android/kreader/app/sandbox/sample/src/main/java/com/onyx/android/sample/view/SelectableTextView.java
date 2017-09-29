package com.onyx.android.sample.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wangxu on 17-8-5.
 */

public class SelectableTextView extends TextView {

    public SelectableTextView(Context context) {
        this(context, null);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelection(int start, int end) {
        SpannableString spannableString = new SpannableString(getText());
        spannableString.setSpan(new BackgroundColorSpan(Color.GRAY), start , end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(spannableString);
    }

    public void clearSelection() {
        SpannableString spannableString = new SpannableString(getText());
        spannableString.setSpan(new BackgroundColorSpan(Color.WHITE), 0, length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(spannableString);
    }
}
