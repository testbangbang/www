package com.onyx.kreader.host.math;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 2/12/16.
 *
 */
public class PositionSnapshot {

    private String layoutType;
    private String pageName;
    private RectF displayRect; // page display rect in view port coordinates system with actual scale.
    private float actualScale = 1.0f;
    private int specialScale;

    static public PositionSnapshot createSnapshot(final String type, final PageInfo pageInfo, int specialScale)  {
        PositionSnapshot snapshot = new PositionSnapshot();
        snapshot.layoutType = type;
        snapshot.actualScale = pageInfo.getActualScale();
        snapshot.displayRect = new RectF(pageInfo.getDisplayRect());
        snapshot.specialScale = specialScale;
        return snapshot;
    }


}
