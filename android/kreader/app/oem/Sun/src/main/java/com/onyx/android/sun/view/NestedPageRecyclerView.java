package com.onyx.android.sun.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by li on 2017/11/9.
 */

public class NestedPageRecyclerView extends PageRecyclerView {
    private boolean isIntercepted = false;

    public NestedPageRecyclerView(Context context) {
        super(context);
    }

    public NestedPageRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isIntercepted) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void setIntercepted(boolean isIntercepted) {
        this.isIntercepted = isIntercepted;
    }
}
