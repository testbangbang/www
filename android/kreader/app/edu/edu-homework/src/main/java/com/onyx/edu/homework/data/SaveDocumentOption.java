package com.onyx.edu.homework.data;

/**
 * Created by lxm on 2018/1/3.
 */

public class SaveDocumentOption {

    public boolean finishAfterSave;
    public boolean resumeDrawing;
    public boolean render;
    public boolean showLoading;

    public SaveDocumentOption(boolean finishAfterSave, boolean resumeDrawing, boolean render, boolean showLoading) {
        this.finishAfterSave = finishAfterSave;
        this.resumeDrawing = resumeDrawing;
        this.render = render;
        this.showLoading = showLoading;
    }

    public static SaveDocumentOption onPageSaveOption() {
        return new SaveDocumentOption(true, false, false, false);
    }

    public static SaveDocumentOption onStopSaveOption() {
        return new SaveDocumentOption(false, false, true, false);
    }
}
