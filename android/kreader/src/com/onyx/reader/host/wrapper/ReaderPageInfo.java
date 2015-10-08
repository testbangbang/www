package com.onyx.reader.host.wrapper;


import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/6/15.
 * This class is used to save
 * 1. page natural size
 * 2. page crop content region.
 * 3. page display rect in document coordinates system.
 * 4. screen rect (aka, viewport rect) in document coordinates system.
 * 5. page rect in screen coordinates system.
 * 6. page position.
 * Host Coordinates system and plugin coordinates system.
 */
public class ReaderPageInfo {

    public RectF pageNaturalRect = new RectF();
    public RectF autoCropContentRegion;    // region when zoom to page.
    public float autoCropScale;

    public int pageNaturalOrientation;      // degree 0, 90, 180, 270.
    public RectF manualRegion;

    // page position in document coordinate system.
    public RectF pageRectInHost = new RectF();

    // viewport in document coordinate system.
    public RectF viewportRectInHost = new RectF();
    public RectF pageRectInScreen = new RectF();
    public float pageDisplayScale;
    public int pageNumber;
    public String location;



    public void clearAutoCropInfo() {
        autoCropContentRegion = null;
        autoCropScale = 0;
    }

    public int autoCropContentRegionHeight(double scale) {
        return (int)(autoCropContentRegion.height() *  scale / autoCropScale);
    }

    // get document position from screen position
    public PointF documentPointFromScreenPoint(float screenX, float screenY) {
        return new PointF((float)((screenX - pageRectInScreen.left) / pageDisplayScale),
                (float)((screenY - pageRectInScreen.top) / pageDisplayScale));
    }

    public PointF screenPointFromDocument(int docX, int docY) {
        return new PointF(docX + pageRectInHost.left - pageRectInScreen.left,
                docY + pageRectInHost.top - pageRectInScreen.top);
    }
}
