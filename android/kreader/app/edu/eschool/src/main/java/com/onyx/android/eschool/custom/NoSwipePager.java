package com.onyx.android.eschool.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/28.
 */
public class NoSwipePager extends ViewPager {

    private List<Class<?>> filterScrollableViewList = new ArrayList<>();

    public NoSwipePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addFilterScrollableViewClass(Class<?> tClass) {
        filterScrollableViewList.add(tClass);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        for (Class<?> viewClass : filterScrollableViewList) {
            if (viewClass.isInstance(v)) {
                return true;
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
}
