package com.onyx.edu.reader.ui.events;

/**
 * Created by zhuzeng on 10/13/16.
 */

public class SlideshowProgressEvent {
    private int totalPage;
    private int currentPage;
    private int startBatteryPercentLevel;
    private int currentBatteryPercentLevel;

    public SlideshowProgressEvent(int totalPage, int currentPage, int startBatteryPercentLevel,
                                  int currentBatteryPercentLevel) {
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.startBatteryPercentLevel = startBatteryPercentLevel;
        this.currentBatteryPercentLevel = currentBatteryPercentLevel;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getStartBatteryPercentLevel() {
        return startBatteryPercentLevel;
    }

    public int getCurrentBatteryPercentLevel() {
        return currentBatteryPercentLevel;
    }
}
