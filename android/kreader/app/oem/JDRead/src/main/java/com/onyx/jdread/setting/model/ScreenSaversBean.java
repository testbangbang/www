package com.onyx.jdread.setting.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hehai on 18-1-6.
 */

public class ScreenSaversBean implements Serializable {
    private String display;
    private boolean checked;
    private List<String> pics;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }
}
