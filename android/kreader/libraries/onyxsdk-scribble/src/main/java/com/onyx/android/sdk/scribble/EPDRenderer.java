package com.onyx.android.sdk.scribble;


import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by zhuzeng on 4/21/16.
 * Could be replaced by enableEpdHandwriting now.
 */
public class EPDRenderer {

    public static void moveTo(float x, float y, float strokeWidth) {
        EpdController.moveTo(x, y, strokeWidth);
    }

    public static void quadTo(float x, float y, final UpdateMode updateMode) {
        EpdController.quadTo(x, y, updateMode);
    }


}
