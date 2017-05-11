package com.onyx.kreader.ui.data;

import android.graphics.RectF;

import com.onyx.android.cropimage.data.CropArgs;
import com.onyx.android.sdk.data.ReaderPointMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 5/22/15.
 */
public class ReaderCropArgs {

    public enum NavigationMode {
        SINGLE_PAGE_MODE,
        ROWS_LEFT_TO_RIGHT_MODE,
        ROWS_RIGHT_TO_LEFT_MODE,
        COLUMNS_LEFT_TO_RIGHT_MODE,
        COLUMNS_RIGHT_TO_LEFT_MODE,
    }

    public enum CropPageMode {
        None,
        AUTO_CROP_PAGE,
        TWO_CROP_PAGE,
        MANUAL_CROP_PAGE,
        MANUAL_CROP_PAGE_BY_ODD_AND_EVEN,
    }

    private CropArgs cropArgs = new CropArgs();
    private NavigationMode navigationMode;
    private CropPageMode cropPageMode; // crop mode, auto or manual
    private List<RectF> manualCropDocRegions = new ArrayList<>(); // in case odd/even page crop.
    private List<ReaderPointMatrix> manualPointMatrixList = new ArrayList<>();

    public CropArgs getCropArgs() {
        return cropArgs;
    }

    public void setRows(int rows) {
        cropArgs.rows = rows;
        if (rows > 1) {
            cropArgs.manualSplitPage = true;
        }
    }

    public void setColumns(int columns) {
        cropArgs.columns = columns;
        if (columns > 1) {
            cropArgs.manualSplitPage = true;
        }
    }

    public NavigationMode getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(NavigationMode mode) {
        this.navigationMode = mode;
    }

    public CropPageMode getCropPageMode() {
        return cropPageMode;
    }

    public void setCropPageMode(CropPageMode mode) {
        this.cropPageMode = mode;
        if (mode == CropPageMode.MANUAL_CROP_PAGE ||
                mode == CropPageMode.MANUAL_CROP_PAGE_BY_ODD_AND_EVEN) {
            cropArgs.manualCropPage = true;
        }
    }

    public List<RectF> getManualCropDocRegions() {
        return manualCropDocRegions;
    }

    public List<ReaderPointMatrix> getManualPointMatrixList() {
        return manualPointMatrixList;
    }
}
