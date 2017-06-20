package com.onyx.android.sdk.reader.host.math;

import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.PageConstants;

import java.util.List;

/**
 * Created by zhuzeng on 2/12/16.
 * To record position.
 */
public class PositionSnapshot {

    public String layoutType;
    public String pageName;
    public String pagePosition;
    public RectF viewport;              // viewport absolute position.
    public RectF viewportOffsetRect;    // viewport offset in page
    public float actualScale = 1.0f;
    public int specialScale = PageConstants.SCALE_INVALID;
    public int subScreenIndex = -1;

    static public PositionSnapshot fromSnapshotKey(final String string)  {
        PositionSnapshot snapshot = JSON.parseObject(string, PositionSnapshot.class);
        return snapshot;
    }

    static public PositionSnapshot snapshot(final String type, final PageInfo pageInfo, final RectF viewport, int specialScale)  {
        return snapshot(type, pageInfo, viewport, specialScale, -1);
    }

    static public PositionSnapshot snapshot(final String type, final PageInfo pageInfo, final RectF viewport, int specialScale, int subScreenIndex)  {
        PositionSnapshot snapshot = new PositionSnapshot();
        snapshot.pageName = pageInfo.getName();
        snapshot.pagePosition = pageInfo.getPosition();
        snapshot.layoutType = type;
        snapshot.actualScale = pageInfo.getActualScale();
        snapshot.viewport = new RectF(viewport);
        snapshot.viewportOffsetRect = new RectF(viewport);
        PageUtils.translateCoordinates(snapshot.viewportOffsetRect, pageInfo.getPositionRect());
        snapshot.specialScale = specialScale;
        snapshot.subScreenIndex = subScreenIndex;
        return snapshot;
    }

    static public String cacheKey(final List<PageInfo> list) {
        return JSON.toJSONString(list);
    }

    public String key() {
        return JSON.toJSONString(this);
    }


}
