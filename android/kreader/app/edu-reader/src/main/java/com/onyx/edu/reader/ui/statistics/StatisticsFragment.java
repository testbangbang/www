package com.onyx.edu.reader.ui.statistics;

import android.support.v4.app.Fragment;

import com.onyx.android.sdk.data.model.StatisticsResult;

/**
 * Created by ming on 2017/2/14.
 */

public abstract class StatisticsFragment extends Fragment {

    protected StatisticsResult statisticsResult;

    public void setStatisticsResult(StatisticsResult statisticsResult) {
        this.statisticsResult = statisticsResult;
        if (isAdded()) {
            refreshStatistics(statisticsResult);
        }
    }

    public abstract void refreshStatistics(StatisticsResult statisticsResult);
}
