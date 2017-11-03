package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/9/22.
 */
public class ExportHtmlSuccessEvent {
    private String filePath;

    public ExportHtmlSuccessEvent(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
