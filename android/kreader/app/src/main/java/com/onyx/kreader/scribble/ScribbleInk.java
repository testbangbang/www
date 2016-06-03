package com.onyx.kreader.scribble;


import android.text.method.Touch;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.onyx.kreader.scribble.data.TouchPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/24/15.
javah -classpath ./:../../../OnyxAndroidSDK/bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar -jni com.onyx.android.sdk.internal.scribble.ScribbleInk
 */

public class ScribbleInk {

    static {
        System.loadLibrary("onyx_ink");
    }

    static public class PressureEntry {
        public int pressLimit;
        public float normalizedWidth;   /// ratio
        public float displayWidth;

        public PressureEntry(int p, float nw, float dw) {
            pressLimit = p;
            normalizedWidth = nw;
            displayWidth = dw * nw;
        }
    }

    static private List<PressureEntry> map = new ArrayList<PressureEntry>();
    static private int MAX_LEVEL = 100;
    static private int DEFAULT_DELTA = 2;
    static private float SPEED_FACTOR = 4.0f;
    static private int MAX_SPEED = 10;


    static public native void config(int maxLevel, int delta, final float speedFactor, final int maxSpeed);
    static public native void clearEntryList();
    static public native void addToListEntry(int pressure, float normalizedWidth, float displayWidth);
    static public native float smoothWidthNative(float baseWidth, float newX, float newY, float newPressure, long newEventTime,
                                               float oldX, float oldY, float oldPressure, long oldEventTime,
                                               boolean calculateSpeed);
    static public native void resetLastIndex();

    static int velocity(final TouchPoint newPoint, final TouchPoint oldPoint, final float factor) {
        float dx = newPoint.getX() - oldPoint.getX();
        float dy = newPoint.getY() - oldPoint.getY();
        float dist = dx * dx + dy * dy;
        long duration = (newPoint.getTimestamp() - oldPoint.getTimestamp()) + 1;
        return (int)(factor * (dist  / duration));
    }

    static public int smoothWidth(final TouchPoint newPoint, final TouchPoint oldPoint, int last) {
        int value = (int)(newPoint.getPressure() * MAX_LEVEL);
        int index = 0;
        for(PressureEntry entry : map) {
            if (entry.pressLimit >= value) {
                break;
            }
            ++index;
        }
        int delta = DEFAULT_DELTA;
        if (oldPoint != null) {
            int v = Math.min(velocity(newPoint, oldPoint, SPEED_FACTOR), MAX_SPEED);
            index -= v;
        }

        if (index <= last - delta) {
            index = last - delta;
        } else if (index >= last + delta) {
            index = last + delta;
        }
        if (index < 0) {
            index = 0;
        } else if (index >= map.size() - 1) {
            index = map.size() - 1;
        }

        return index;
    }

    static public float displayWidth(int index) {
        if (index < 0) {
            index = 0;
        } else if (index >= map.size() - 1) {
            index = map.size() - 1;
        }
        return map.get(index).displayWidth;
    }


    // mapping from pressure to normalized normalizedWidth.
    // display stroke normalizedWidth: normalized normalizedWidth * base display normalizedWidth * display scale * selected thickness
    static public void resetLookupTable(final List<JSONObject> pm, final List<JSONObject> vm, float baseWidth,  final float speedFactor, final int maxSpeed) {
        if (pm == null) {
            return;
        }
        map.clear();
        clearEntryList();
        for (int i = 0; i < pm.size(); ++i) {
            JSONObject object = pm.get(i);
            PressureEntry entry = new PressureEntry(object.getInteger("p"), object.getFloat("nw"), baseWidth);
            addToListEntry(entry.pressLimit, entry.normalizedWidth, entry.displayWidth);
            map.add(entry);
        }
        MAX_SPEED = maxSpeed;
        SPEED_FACTOR = speedFactor;
        config(MAX_LEVEL, DEFAULT_DELTA, SPEED_FACTOR, MAX_SPEED);
    }

}
