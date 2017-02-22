package com.onyx.android.sdk.scribble.api;

import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by ming on 2017/2/22.
 */

public abstract class RawInputCallback {
    // when received pen down or stylus button
    public abstract void onBeginRawData();

    // when pen released.
    public abstract void onRawTouchPointListReceived(final TouchPointList pointList);

    // caller should render the page here.
    public abstract void onBeginErasing();

    // caller should do hit test in current page, remove shapes hit-tested.
    public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);
}
