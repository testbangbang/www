package com.onyx.android.sdk.reader.host.wrapper;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutUtils {

    /**
     * The whole host coordinates system. Actually, we only need the size. the top left is always (0, 0)
     * When rendering, need to calculate the view point in host.
     */
    private RectF hostRect = new RectF();

    /**
     * Current viewportInPage(screen) in host coordinates system.
     */
    private RectF viewportInHost = new RectF();

    /**
     * convert point in view to point in host.
     * @param pointInView
     */
    public void viewPointToHostPoint(final PointF pointInView) {
        float x = pointInView.x + viewportInHost.left;
        float y = pointInView.y + viewportInHost.top;
        pointInView.set(x, y);
    }

    public void hostPointToPagePoint(final PointF pointInHost, final RectF pageRectInHost) {
        float x = pointInHost.x - pageRectInHost.left;
        float y = pointInHost.y - pageRectInHost.top;
        pointInHost.set(x, y);
    }

    /**
     * Covert point in view to point in page
     * @param pointInView
     * @param pageRectInHost
     */
    public void viewPointToPagePoint(final PointF pointInView, final RectF pageRectInHost) {
        viewPointToHostPoint(pointInView);
        hostPointToPagePoint(pointInView, pageRectInHost);
    }



}
