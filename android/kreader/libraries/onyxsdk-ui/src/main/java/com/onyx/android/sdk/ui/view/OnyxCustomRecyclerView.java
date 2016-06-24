package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;
import com.onyx.android.sdk.ui.utils.TouchDirection;

/**
 * Created by solskjaer49 on 16/6/23 12:01.
 */

public class OnyxCustomRecyclerView extends RecyclerView {
    //DefaultValue
    int touchDirection = TouchDirection.HORIZONTAL;
    private float lastX = 0f;
    private float lastY = 0f;

    public OnyxCustomRecyclerView(Context context) {
        super(context);
    }

    public OnyxCustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OnyxCustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                switch (detectDirection(ev)) {
                    case PageTurningDirection.PREV:
                        prevPage();
                        break;
                    case PageTurningDirection.NEXT:
                        nextPage();
                        break;
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void prevPage() {

    }

    private void nextPage() {

    }

    private int detectDirection(MotionEvent currentEvent) {
        switch (touchDirection) {
            case TouchDirection.HORIZONTAL:
                return PageTurningDetector.detectHorizontalTuring(getContext(), (int) (currentEvent.getX() - lastX));
            case TouchDirection.VERTICAL:
                return PageTurningDetector.detectVerticalTuring(getContext(), (int) (currentEvent.getY() - lastY));
            case TouchDirection.BOTH:
                return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX),
                        (int) (currentEvent.getY() - lastY));
            default:
                return PageTurningDetector.detectHorizontalTuring(getContext(), (int) (currentEvent.getX() - lastX));
        }
    }
}
