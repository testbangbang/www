package com.onyx.kreader.host.math;

import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.List;

/**
 * Created by zhuzeng on 2/12/16.
 * To record position.
 */
public class PositionSnapshot {

    public String layoutType;
    public String pageName;
    public RectF viewportOffsetRect; // viewport offset in page
    public float actualScale = 1.0f;
    public int specialScale = ReaderConstants.SCALE_INVALID;

    static public PositionSnapshot fromSnapshotKey(final String string)  {
        PositionSnapshot snapshot = JSON.parseObject(string, PositionSnapshot.class);
        return snapshot;
    }

    static public PositionSnapshot snapshot(final String type, final PageInfo pageInfo, final RectF viewport, int specialScale)  {
        PositionSnapshot snapshot = new PositionSnapshot();
        snapshot.pageName = pageInfo.getName();
        snapshot.layoutType = type;
        snapshot.actualScale = pageInfo.getActualScale();
        snapshot.viewportOffsetRect = new RectF(viewport);
        PageUtils.translateCoordinates(snapshot.viewportOffsetRect, pageInfo.getPositionRect());
        snapshot.specialScale = specialScale;
        return snapshot;
    }

    static public String cacheKey(final List<PageInfo> list) {
        return JSON.toJSONString(list);
    }

    public String key() {
        return JSON.toJSONString(this);
    }


}
