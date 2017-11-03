package com.onyx.android.sdk.data.model;

/**
 * Created by zhouzhiming on 2017/9/20.
 */
public class CreateInformalEssayBean {
    public int __v;
    public String updatedAt;
    public String createdAt;
    public String user;
    public String _id;
    public String title;
    public String wordNumber;
    public String content;
    public long currentTime;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
