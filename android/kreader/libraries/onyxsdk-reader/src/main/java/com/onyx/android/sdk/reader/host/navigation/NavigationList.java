package com.onyx.android.sdk.reader.host.navigation;

import android.graphics.RectF;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.ReaderPointMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/16/15.
 * Make sure the rectangles are normalized in [0, 1]
 */
public class NavigationList {

    public List<RectF> subScreenList = new ArrayList<RectF>();
    public int currentIndex = -1;
    public RectF limitedRect;

    static public NavigationList rowsLeftToRight(final ReaderPointMatrix pointMatrix, final RectF limited) {
        final int rows = pointMatrix.getOriginRows();
        final int cols = pointMatrix.getOriginCols();
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                RectF sub = getSubScreenRect(child, pointMatrix, rows, cols, r, c);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList rowsLeftToRight(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                RectF sub = getEquallySubScreenRect(child, rows, cols, r, c);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList rowsRightToLeft(final ReaderPointMatrix pointMatrix, final RectF limited) {
        final int rows = pointMatrix.getOriginRows();
        final int cols = pointMatrix.getOriginCols();
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                RectF sub = getSubScreenRect(child, pointMatrix, rows, cols, r, cols -c - 1);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList rowsRightToLeft(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                RectF sub = getEquallySubScreenRect(child, rows, cols, r, cols - c - 1);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsLeftToRight(final ReaderPointMatrix pointMatrix, final RectF limited) {
        final int rows = pointMatrix.getOriginRows();
        final int cols = pointMatrix.getOriginCols();
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                RectF sub = getSubScreenRect(child, pointMatrix, rows, cols, r, c);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsLeftToRight(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                RectF sub = getEquallySubScreenRect(child, rows, cols, r, c);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsRightToLeft(final ReaderPointMatrix pointMatrix, final RectF limited) {
        final int rows = pointMatrix.getOriginRows();
        final int cols = pointMatrix.getOriginCols();
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                RectF sub = getSubScreenRect(child, pointMatrix, rows, cols, r, cols - c - 1);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsRightToLeft(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                RectF sub = getEquallySubScreenRect(child, rows, cols, r, cols - c - 1);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    private static RectF getSubScreenRect(RectF parent, ReaderPointMatrix pointMatrix, int rows, int cols, int row, int col) {
        boolean useLeftEdge = cols == 1 || col <= 0;
        boolean useTopEdge = rows == 1 || row <= 0;
        boolean useRightEdge = cols == 1 || col >= pointMatrix.cols();// screensSplitPoints[0].length;
        boolean useBottomEdge = rows == 1 || row >= pointMatrix.rows();//screensSplitPoints.length;

        final Float left, top, right, bottom;
        // separating single row/column screen to simplify the logic of sub screen computation
        if (rows <= 1) {
            left = useLeftEdge ? 0.0f : pointMatrix.get(0, col - 1).x;// screensSplitPoints[0][column - 1].x;
            top = 0.0f;
            right = useRightEdge ? 1.0f : pointMatrix.get(0, col).x;// screensSplitPoints[0][column].x;
            bottom = 1.0f;
        } else if (cols <= 1) {
            left = 0.0f;
            top = useTopEdge ? 0.0f : pointMatrix.get(row - 1, 0).y;// screensSplitPoints[row - 1][0].y;
            right = 1.0f;
            bottom = useBottomEdge ? 1.0f : pointMatrix.get(row, 0).y;// screensSplitPoints[row][0].y;
        } else {
            left = useLeftEdge ? 0.0f : pointMatrix.safeGetX(useTopEdge ? 0 : row -1, col - 1);
            top = useTopEdge ? 0.0f : pointMatrix.safeGetY(row - 1, useLeftEdge ? 0 : col - 1);
            right = useRightEdge ? 1.0f : pointMatrix.safeGetX(useBottomEdge ? row - 1 : row, col);
            bottom = useBottomEdge ? 1.0f : pointMatrix.safeGetY(row, useRightEdge ? col - 1 : col);
        }
        return new RectF(parent.left + parent.width() * left,
                parent.top + parent.height() * top,
                parent.left + parent.width() * right,
                parent.top + parent.height() * bottom);
    }

    private static RectF getEquallySubScreenRect(RectF parent, int rows, int cols, int row, int col) {
        float left = parent.left + parent.width() / cols * col;
        float right = left + parent.width() / cols;
        float top = parent.top + parent.height() / rows * row;
        float bottom = top + parent.height() / rows;
        return new RectF(left, top, right, bottom);
    }

    public NavigationList() {
    }

    public List<RectF> getSubScreenList() {
        return subScreenList;
    }

    public void setLimitedRect(final RectF limit) {
        limitedRect = limit;
    }

    @JSONField(serialize = false)
    public int getSubScreenCount() {
        return subScreenList.size();
    }

    @JSONField(serialize = false)
    public int getLastSubScreenIndex() {
        if (subScreenList.size() > 0) {
            return subScreenList.size() - 1;
        }
        return 0;
    }

    public final RectF getLimitedRect() {
        return limitedRect;
    }

    public void addAll(final List<RectF> list) {
        subScreenList.clear();
        subScreenList.addAll(list);
    }

    public boolean hasNext() {
        if (currentIndex >= subScreenList.size() - 1) {
            return false;
        }
        return true;
    }

    public RectF next() {
        if (hasNext()) {
            ++currentIndex;
            return getCurrent();
        }
        return null;
    }

    public boolean hasPrevious() {
        if (currentIndex <= 0) {
            return false;
        }
        return true;
    }

    public RectF previous() {
        if (hasPrevious()) {
            --currentIndex;
            return getCurrent();
        }
        return null;
    }

    public RectF first() {
        currentIndex = 0;
        return getCurrent();
    }

    public RectF last() {
        currentIndex = subScreenList.size() - 1;
        return getCurrent();
    }

    public RectF gotoSubScreen(int index) {
        if (index < 0 || index >= subScreenList.size()) {
            return null;
        }
        currentIndex = index;
        return getCurrent();
    }

    public RectF getCurrent() {
        if (currentIndex >= 0 && currentIndex < subScreenList.size()) {
            RectF current = new RectF(subScreenList.get(currentIndex));
            if (getLimitedRect() != null) {
                current.intersect(getLimitedRect());
            }
            return current;
        }
        return null;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
    }


}
