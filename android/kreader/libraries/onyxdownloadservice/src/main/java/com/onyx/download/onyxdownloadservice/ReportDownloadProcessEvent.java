package com.onyx.download.onyxdownloadservice;

/**
 * Created by 12 on 2017/3/24.
 */

public class ReportDownloadProcessEvent {
    private int reference;
    private String title;
    private String remoteUri;
    private String localUri;
    private int state;
    private long finished;
    private long total;
    private long percentage;

    public ReportDownloadProcessEvent(int reference, String title, String remoteUri, String localUri, int state, long finished, long total, long percentage) {
        this.reference = reference;
        this.title = title;
        this.remoteUri = remoteUri;
        this.localUri = localUri;
        this.state = state;
        this.finished = finished;
        this.total = total;
        this.percentage = percentage;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemoteUri() {
        return remoteUri;
    }

    public void setRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPercentage() {
        return percentage;
    }

    public void setPercentage(long percentage) {
        this.percentage = percentage;
    }
}
