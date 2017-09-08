package com.onyx.android.sample.device;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.onyx.android.sample.BuildConfig;
import com.onyx.android.sample.data.GObject;
import com.onyx.android.sample.utils.RawResourceUtil;
import com.onyx.android.sample.utils.StringUtils;

/**
 * Created by zhuzeng on 9/10/15.
 */
public class DeviceConfig {

    static private DeviceConfig globalInstance;
    static private String TAG = DeviceConfig.class.getSimpleName();
    private GObject backend;

    static public final String EPD_WIDTH = "epd_width";
    static public final String EPD_HEIGHT = "epd_height";
    static public final String EPD_POST_ORIENTATION = "epd_post_orientation";
    static public final String EPD_POST_TX = "epd_post_tx";
    static public final String EPD_POST_TY = "epd_post_ty";

    static public final String VIEW_POST_ORIENTATION = "view_post_orientation";
    static public final String VIEW_POST_TX = "view_post_tx";
    static public final String VIEW_POST_TY = "view_post_ty";

    static public final String TOUCH_WIDTH = "touch_width";
    static public final String TOUCH_HEIGHT = "touch_height";

    static public final String USE_RAW_INPUT = "use_raw_input";
    static public final String SINGLE_TOUCH = "single_touch";
    static public final String FINGER_ERASING = "finger_erasing";
    static public final String DISABLE_BIG_PEN = "no_big_pen";

    static public final String SHORTCUT_DRAWING = "shortcut_drawing";
    static public final String SHORTCUT_ERASING = "shortcut_erasing";

    static public final String SUPPORT_COLOR = "support_color";
    static public final String ERASER_RADIUS = "eraser_radius";
    static public final String DEFAULT_STROKE_COLOR = "default_stroke_color";

    static public boolean useDebugConfig = false;


    public float getEpdWidth() {
        if (!backend.hasKey(EPD_WIDTH)) {
            return 1200f;
        }
        return backend.getFloat(EPD_WIDTH);
    }

    public float getEpdHeight() {
        if (!backend.hasKey(EPD_HEIGHT)) {
            return 825;
        }
        return backend.getFloat(EPD_HEIGHT);
    }

    public int getEpdPostOrientation() {
        if (!backend.hasKey(EPD_POST_ORIENTATION)) {
            return 0;
        }
        return backend.getInt(EPD_POST_ORIENTATION);
    }

    public int getEpdPostTx() {
        if (!backend.hasKey(EPD_POST_TX)) {
            return 0;
        }
        return backend.getInt(EPD_POST_TX);
    }

    public int getEpdPostTy() {
        if (!backend.hasKey(EPD_POST_TY)) {
            return 0;
        }
        return backend.getInt(EPD_POST_TY);
    }

    public int getViewPostOrientation() {
        if (!backend.hasKey(VIEW_POST_ORIENTATION)) {
            return 0;
        }
        return backend.getInt(VIEW_POST_ORIENTATION);
    }

    public int getViewPostTx() {
        if (!backend.hasKey(VIEW_POST_TX)) {
            return 0;
        }
        return backend.getInt(VIEW_POST_TX);
    }

    public int getViewPostTy() {
        if (!backend.hasKey(VIEW_POST_TY)) {
            return 0;
        }
        return backend.getInt(VIEW_POST_TY);
    }


    public float getTouchWidth() {
        if (!backend.hasKey(TOUCH_WIDTH)) {
            return 1.0f;
        }
        return backend.getFloat(TOUCH_WIDTH);
    }

    public float getTouchHeight() {
        if (!backend.hasKey(TOUCH_HEIGHT)) {
            return 1.0f;
        }
        return backend.getFloat(TOUCH_HEIGHT);
    }

    public boolean isShortcutDrawingEnabled() {
        return backend.hasKey(SHORTCUT_DRAWING) && backend.getBoolean(SHORTCUT_DRAWING);
    }

    public boolean isShortcutErasingEnabled() {
        return backend.hasKey(SHORTCUT_ERASING) && backend.getBoolean(SHORTCUT_ERASING);
    }

    public boolean useRawInput() {
        return backend.hasKey(USE_RAW_INPUT) && backend.getBoolean(USE_RAW_INPUT);
    }

    public boolean isSingleTouch() {
        return backend.hasKey(SINGLE_TOUCH) && backend.getBoolean(SINGLE_TOUCH);
    }

    public boolean isEnableFingerErasing() {
        return backend.hasKey(FINGER_ERASING) && backend.getBoolean(FINGER_ERASING);
    }

    public boolean supportBigPen() {
        boolean disable = backend.hasKey(DISABLE_BIG_PEN) && backend.getBoolean(DISABLE_BIG_PEN);
        return !disable;
    }

    public boolean supportColor(){
        return backend.hasKey(SUPPORT_COLOR) && backend.getBoolean(SUPPORT_COLOR, false);
    }

    public float getEraserRadius() {
        if (backend.hasKey(ERASER_RADIUS)) {
            return backend.getFloat(ERASER_RADIUS);
        }
        return 15.0f;
    }

    public int getDefaultStrokeColor() {
        if (backend.hasKey(DEFAULT_STROKE_COLOR)) {
            return backend.getInt(DEFAULT_STROKE_COLOR);
        }
        return Color.BLACK;
    }

    static public DeviceConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new DeviceConfig(context, null);
        }
        return globalInstance;
    }


    static public DeviceConfig sharedInstance(Context context, final String prefix) {
        if (globalInstance == null) {
            globalInstance = new DeviceConfig(context, prefix);
        }
        return globalInstance;
    }

    private DeviceConfig(Context context, final String prefix) {
        backend = objectFromDebugModel(context);
        if (backend != null) {
            Log.i(TAG, "Using debug model.");
            return;
        }

        backend = objectFromManufactureAndModel(context);
        if (backend != null) {
            Log.i(TAG, "Using manufacture model.");
            return;
        }

        backend = objectFromPrefixAndModel(context, prefix);
        if (backend != null) {
            Log.i(TAG, "Using manufacture model.");
            return;
        }

        backend = objectFromModel(context);
        if (backend != null) {
            Log.i(TAG, "Using device model.");
            return;
        }

        Log.i(TAG, "Using default model.");
        backend = objectFromDefaultOnyxConfig(context);
        if (backend == null) {
            backend = new GObject();
        }
    }

    private GObject objectFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromPrefixAndModel(Context context, final String prefix) {
        if (StringUtils.isNullOrEmpty(prefix)) {
            return null;
        }
        final String name = prefix + "_" + Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, "debug");
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDefaultOnyxConfig(Context context) {
        return objectFromRawResource(context, "onyx");
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object = null;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return object;
        }
    }
}
