package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by ming on 16/9/14.
 */
public class DisableScrollGridManager extends GridLayoutManager {
    private boolean canScroll = false;

    public DisableScrollGridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DisableScrollGridManager(Context context) {
        super(context, 1);
    }

    public DisableScrollGridManager(Context context, int orientation, boolean reverseLayout) {
        super(context, 1, orientation, reverseLayout);
    }

    public DisableScrollGridManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public DisableScrollGridManager(Context context, int spanCount, int orientation,
                                    boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
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
