package com.onyx.kreader.host.math;

import android.graphics.RectF;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zhuzeng on 2/12/16.
 * To record position.
 */
public class PositionSnapshot {

    public String layoutType;
    public String pageName;
    public RectF displayRect; // page display rect in view port coordinates system with actual scale.
    public float actualScale = 1.0f;

    static public PositionSnapshot fromSnapshotKey(final String string)  {
        PositionSnapshot snapshot = JSON.parseObject(string, PositionSnapshot.class);
        return snapshot;
    }

    static public String snapshotKey(final String type, final PageInfo pageInfo)  {
        PositionSnapshot snapshot = new PositionSnapshot();
        snapshot.layoutType = type;
        snapshot.actualScale = pageInfo.getActualScale();
        snapshot.displayRect = pageInfo.getDisplayRect();
        return snapshot.key();
    }

    static public String cacheKey(final List<PageInfo> list) {
        return JSON.toJSONString(list);
    }

    public String key() {
        return JSON.toJSONString(this);
    }


}
