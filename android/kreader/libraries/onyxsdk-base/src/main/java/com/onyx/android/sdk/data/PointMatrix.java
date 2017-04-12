package com.onyx.android.sdk.data;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 5/22/15.
 */
public class PointMatrix {

    private int originRows;

    private int originCols;

    private ArrayList<ArrayList<PointF>> data;

    public PointMatrix() {
        resize(1, 1);
    }

    public PointMatrix(int r, int c) {
        resize(r, c);
    }

    public void setData(final ArrayList<ArrayList<PointF>> d) {
        data = d;
    }

    public PointF get(int row, int col) {
        if (row < 0 || row >= data.size()) {
            return null;
        }
        List<PointF> list = data.get(row);
        if (col < 0 || col >= list.size()) {
            return null;
        }
        return list.get(col);
    }

    public float safeGetX(int row, int col) {
        PointF pointF = get(row, col);
        if (pointF != null) {
            return pointF.x;
        }
        return 0;
    }

    public float safeGetY(int row, int col) {
        PointF pointF = get(row, col);
        if (pointF != null) {
            return pointF.y;
        }
        return 0;
    }

    public boolean set(int row, int col, float x, float y) {
        return set(row, col, new PointF(x, y));
    }

    public boolean set(int row, int col, final PointF point) {
        if (row < 0 || row >= data.size()) {
            return false;
        }
        List<PointF> list = data.get(row);
        if (col < 0 || col >= list.size()) {
            return false;
        }
        list.set(col, point);
        return true;
    }

    public int rows() {
        return data.size();
    }

    public int cols() {
        if (rows() <= 0) {
            return 0;
        }
        return data.get(0).size();
    }

    /**
     * when resize, all data will re released.
     * @param newRows
     * @param newCols
     * @return
     */
    public void resize(int newRows, int newCols) {
        originCols = newCols;
        originRows = newRows;
        newRows = newRows <= 1 ? newRows : newRows - 1;
        newCols = newCols <= 1 ? newCols : newCols - 1;
        data = new ArrayList<ArrayList<PointF>>(newRows);
        for(int i = 0; i < newRows; ++i) {
            ArrayList<PointF> list = new ArrayList<PointF>();
            for(int j = 0; j < newCols; ++j) {
                list.add(new PointF());
            }
            data.add(list);
        }
    }

    public ArrayList<ArrayList<PointF>> getData() {
        return data;
    }

    public void distribute(int rows, int cols, float left, float top, float right, float bottom) {
        resize(rows, cols);
        for(int r = 0;  r < rows(); ++r) {
            for(int c = 0; c < cols(); ++c) {
                PointF point = get(r, c);
                point.set(left + (right - left) * (c + 1) / (cols() + 1), top + (bottom - top) * (r + 1) / (rows() + 1));
            }
        }
    }

    public boolean hitTest(float x, float y, float hysteresis, final RefValue<Integer> hitRow, final RefValue<Integer> hitCol, final RefValue<PointF> point) {
        for (int r = 0; r < data.size(); ++r) {
            ArrayList<PointF> list = data.get(r);
            for (int c = 0; c < list.size(); ++c) {
                PointF p = list.get(c);
                if ((Math.abs(p.x - x) < 2 * hysteresis) && (Math.abs(p.y - y) < 2 * hysteresis)) {
                    hitRow.setValue(r);
                    hitCol.setValue(c);
                    point.setValue(p);
                    return true;
                }
            }
        }
        return false;
    }

    public void offset(float dx, float dy) {
        for (int r = 0; r < data.size(); ++r) {
            ArrayList<PointF> list = data.get(r);
            for (int c = 0; c < list.size(); ++c) {
                PointF p = list.get(c);
                p.offset(dx, dy);
            }
        }
    }

    public int getOriginRows() {
        return originRows;
    }

    public int getOriginCols() {
        return originCols;
    }

    public void setOriginRows(int originRows) {
        this.originRows = originRows;
    }

    public void setOriginCols(int originCols) {
        this.originCols = originCols;
    }
}
