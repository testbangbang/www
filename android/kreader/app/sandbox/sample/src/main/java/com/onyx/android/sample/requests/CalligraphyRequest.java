package com.onyx.android.sample.requests;

import com.onyx.android.sample.activity.CalligraphyActivity;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by john on 1/10/2017.
 */

public class CalligraphyRequest extends BaseRequest {

    private volatile float x, y;
    private volatile boolean finished;

    public CalligraphyRequest(float px, float py, boolean f) {
        x = px;
        y = py;
        finished = f;
    }

    public void execute(CalligraphyActivity activity) {
        activity.points.add(new TouchPoint(x, y, 1, 1, System.currentTimeMillis()));
        //activity.drawStroke(activity.lastX, activity.lastY, x, y, finished);
        activity.drawStroke2(activity.lastX, activity.lastY, x, y, finished);
        activity.lastX = x;
        activity.lastY = y;
        if (finished) {
            activity.points.clear();
        }

    }
}
