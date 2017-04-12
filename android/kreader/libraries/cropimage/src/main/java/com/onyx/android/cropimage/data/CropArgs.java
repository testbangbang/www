package com.onyx.android.cropimage.data;

import android.graphics.Rect;

import com.onyx.android.sdk.data.ReaderPointMatrix;

/**
 * Created by Joy on 2016/4/21.
 */
public class CropArgs {
    public Rect selectionRect;
    public ReaderPointMatrix pointMatrixList;
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
