package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public abstract class RegionBaseAction extends BaseAction {
    public static final int WIDTH_AVERAGE_BLOCK = 3;
    public static final int HEIGHT_AVERAGE_BLOCK = 5;
    private int width;
    private int height;
    private int x;
    private int y;
    private List<Rect> rects = new ArrayList<>();

    public RegionBaseAction(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public List<Rect> getRects() {
        return rects;
    }

    public void addRect(Rect rect) {
        this.rects.add(rect);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void setRegion();

    public boolean isContains() {
        for (Rect rect : getRects()) {
            if(rect.contains(getX(),getY())){
                return true;
            }
        }
        return false;
    }
}
