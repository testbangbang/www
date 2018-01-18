package com.onyx.jdread.personal.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class PointsForModel {
    private List<PointsForData> list = new ArrayList<>();

    public List<PointsForData> getList() {
        return list;
    }

    public void loadData() {
        String[] days = ResManager.getResStringArray(R.array.points_for_days);
        String[] points = ResManager.getResStringArray(R.array.points_for_points);
        for (int i = 0; i < days.length; i++) {
            PointsForData data = new PointsForData();
            data.setDays(days[i]);
            data.setPoints(points[i]);
            list.add(data);
        }
    }
}
