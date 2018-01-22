package com.onyx.jdread.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.ui.view.PageRecyclerView;

/**
 * Created by hehai on 18-1-22.
 */

public class NonClickableRelativeLayout extends RelativeLayout {

    public NonClickableRelativeLayout(Context context) {
        super(context);
    }

    public NonClickableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonClickableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
