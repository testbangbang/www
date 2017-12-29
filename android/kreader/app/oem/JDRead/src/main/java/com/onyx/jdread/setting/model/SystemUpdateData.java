package com.onyx.jdread.setting.model;

import android.databinding.BaseObservable;

/**
 * Created by li on 2017/12/26.
 */

public class SystemUpdateData extends BaseObservable {
    private String versionTitle;
    private String version;
    private boolean showDownloaded;
    private String updateDes;
    private String noticeMessage;
    private int progress;
    private boolean showProgress;

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        notifyChange();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyChange();
    }

    public String getVersionTitle() {
        return versionTitle;
    }

    public void setVersionTitle(String versionTitle) {
        this.versionTitle = versionTitle;
        notifyChange();
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage;
        notifyChange();
    }

    public String getUpdateDes() {
        return updateDes;
    }

    public void setUpdateDes(String updateDes) {
        this.updateDes = updateDes;
        notifyChange();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        notifyChange();
    }

    public boolean getShowDownloaded() {
        return showDownloaded;
    }

    public void setShowDownloaded(boolean showDownloaded) {
        this.showDownloaded = showDownloaded;
        notifyChange();
    }
}
