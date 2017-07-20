package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by ming on 16/9/14.
 */
public class DisableScrollLinearManager extends LinearLayoutManager {

    private boolean canScroll = false;

    public DisableScrollLinearManager(Context context) {
        super(context);
    }

    public DisableScrollLinearManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DisableScrollLinearManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnable(boolean enable) {
        this.canScroll = enable;
    }

    @Override
    public boolean canScrollVertically() {
        return canScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return canScroll;
    }
}
