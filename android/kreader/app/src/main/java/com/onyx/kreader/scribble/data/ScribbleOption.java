package com.onyx.kreader.scribble.data;

import android.graphics.Color;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class ScribbleOption {

    public float thickness = 1.0f;
    public float baseRenderWidth = 1.0f;
    public float displayRatio = 1.0f;
    public boolean pressureSensitive = false;
    public boolean useQuadForDFB = true;
    public int background = Color.WHITE;
    public boolean screenshotAsBackground = false;
    public boolean useResourceBitmap = false;

    public ScribbleOption() {
    }

    static public ScribbleOption defaultOption() {
        return new ScribbleOption();
    }


}
