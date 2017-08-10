package com.onyx.android.sdk.data;

import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by joy on 8/10/17.
 */

public class WindowParameters {
    public int width = ViewGroup.LayoutParams.MATCH_PARENT;
    public int height = ViewGroup.LayoutParams.MATCH_PARENT;
    public int gravity = Gravity.BOTTOM;

    public WindowParameters() {
    }

    public WindowParameters(int w, int h, int g) {
        width = w;
        height = h;
        gravity = g;
    }

    public void update(int w, int h, int g) {
        width = w;
        height = h;
        gravity = g;
    }
}
