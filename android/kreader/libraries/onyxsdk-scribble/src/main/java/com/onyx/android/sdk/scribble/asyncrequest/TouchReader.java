package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.asyncrequest.event.ViewTouchEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchReader {

    private Rect limitRect = new Rect();
    private EventBus eventBus;

    public TouchReader(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public TouchReader setLimitRect(Rect softwareLimitRect) {
        this.limitRect = softwareLimitRect;
        return this;
    }

    public void processTouchEvent(final MotionEvent motionEvent) {
        eventBus.post(new ViewTouchEvent(motionEvent));
    }

    public boolean checkTouchPoint(final TouchPoint touchPoint) {
        return limitRect.contains((int) touchPoint.x, (int) touchPoint.y);
    }

    public boolean checkTouchPointList(final TouchPointList touchPointList) {
        if (touchPointList == null || touchPointList.size() == 0) {
            return false;
        }
        List<TouchPoint> touchPoints = touchPointList.getPoints();
        for (TouchPoint touchPoint : touchPoints) {
            if (!checkTouchPoint(touchPoint)) {
                return false;
            }
        }
        return true;
    }

}
