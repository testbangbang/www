package com.onyx.android.cropimage.data;

import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Joy on 2016/4/21.
 */
public class CropArgs {
    public Rect selectionRect;
    public PointMatrix pointMatrixList;
    public int rows;
    public int columns;
    public boolean manualCropPage = false;
    public boolean manualSplitPage = false;

    public boolean manualCropPage() {
        return manualCropPage;
    }

    public boolean manualSplitPage() {
        return manualSplitPage;
    }
}
