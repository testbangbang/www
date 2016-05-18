package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.graphics.Color;

import com.onyx.android.sdk.data.cms.OnyxScribble;
import com.onyx.android.sdk.data.sys.OnyxSysCenter;

import java.util.Date;

/**
 * Created by Joy on 14-2-18.
 */
public class ScribbleFactory {
    private static final int DEFAULT_THICKNESS = 6;

    /**
     * color in ARGB
     */
    private int mColor = Color.BLACK;
    private int mThickness = -1;
    private boolean pressureSensitive = false;

    private static ScribbleFactory sInstance = new ScribbleFactory();

    public static ScribbleFactory singleton()
    {
        return sInstance;
    }

    private ScribbleFactory()
    {

    }

    /**
     * create new scribble with page, color, thickness, update time initialized
     *
     * @param page
     * @param zoomFactor
     * @return
     */
    public OnyxScribble newScribble(Context context, int page, double zoomFactor)
    {
        OnyxScribble scribble = new OnyxScribble();
        scribble.setPage(page);
        scribble.setColor(mColor);
        int thickness = getThickness(context);
        scribble.setThickness(thickness / zoomFactor);
        scribble.setUpdateTime(new Date());

        return scribble;
    }


    /**
     * create new scribble with position, color, thickness, update time initialized
     *
     * @param position the universal scribble position.
     * @param zoomFactor
     * @return
     */
    public OnyxScribble newScribble(final Context context, final String position, double zoomFactor) {
        OnyxScribble scribble = new OnyxScribble();
        scribble.setPosition(position);
        scribble.setColor(mColor);
        scribble.setThickness(getThickness(context) / zoomFactor);
        scribble.setUpdateTime(new Date());
        scribble.generateUniqueId();
        return scribble;
    }

    public int getColor()
    {
        return mColor;
    }

    public void setColor(int color)
    {
        mColor = color;
    }

    public int getThickness(Context context)
    {
        if (mThickness < 0) {
            mThickness = OnyxSysCenter.getScribbleThickness(context, DEFAULT_THICKNESS);
        }
        return mThickness;
    }

    public void setThickness(Context context, int thickness)
    {
        OnyxSysCenter.setScribbleThickness(context, thickness);
        mThickness = thickness;
    }

    public void setPressureSensitive(boolean sensitive) {
        pressureSensitive = sensitive;
    }

    public boolean isPressureSensitive() {
        return pressureSensitive;
    }
}
