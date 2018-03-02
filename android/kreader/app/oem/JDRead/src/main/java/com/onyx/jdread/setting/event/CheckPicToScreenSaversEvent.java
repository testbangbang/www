package com.onyx.jdread.setting.event;

import java.util.List;

/**
 * Created by hehai on 18-1-1.
 */

public class CheckPicToScreenSaversEvent {
    private String display;
    private List<String> pics;

    public List<String> getPics() {
        return pics;
    }

    public String getDisplay() {
        return display;
    }

    public CheckPicToScreenSaversEvent(String display, List<String> pics) {
        this.display = display;
        this.pics = pics;
    }
}
