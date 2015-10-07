package com.onyx.reader.host.wrapper;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/6/15.
 * when calculating sub page navigation, we could consider the whole procedure as followings:
 * 1. generate a list of navigation info.
 * 2. navigate in the list
 * 3. different navigation mode generates different navigation sequence.
 * The navigation info can be calculated according to user behavior or automatically.
 */
public class ReaderNavigationInfo {

    // the view port rect in doc coordinates system with scale.
    // finalScale = scale * currentScale * scaleOf(viewportRect, viewRect)
    // if we always refers to scale = 1.0, we could ignore scale here.
    public RectF viewportRectInDoc = new RectF();

    // the scale that viewport calculated
    public float scale;


}
