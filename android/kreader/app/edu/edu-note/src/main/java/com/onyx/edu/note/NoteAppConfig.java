package com.onyx.edu.note;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.data.DeviceType;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.edu.note.util.Constant;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by solskjaer49 on 2017/5/17 17:56.
 */
public class NoteAppConfig {
    static private String TAG = NoteAppConfig.class.getSimpleName();
    static private final boolean useDebugConfig = false;

    private static NoteAppConfig globalInstance;
    private ArrayList<GObject> backendList = new ArrayList<>();
    static private @DeviceType.DeviceTypeDef
    int currentDeviceType;

    static class TAG {
        public static final String HOME_ACTIVITY_PKG_NAME = "home_activity_pkg";
        public static final String HOME_ACTIVITY_CLS_NAME = "home_activity_cls";
        public static final String SCRIBBLE_ACTIVITY_PKG_NAME = "scribble_activity_pkg";
        public static final String SCRIBBLE_ACTIVITY_CLS_NAME = "scribble_activity_cls";
        public static final String USE_FULL_SCREEN = "full_screen";
        public static final String USE_LINE_LAYOUT = "use_line_layout";
        public static final String SHOW_INPUT_METHOD_INSTANTLY_AFTER_OPEN_DIALOG = "show_input_method_instantly_after_open_dialog";
    }

    /**
     * New backend logic,to avoid duplicate property copy in json.
     * First,put model json in list(if have).
     * Second,put manufacture-based default json in list.
     * Third,put non-manufacture-based default json in list.
     * If debug property has,put it in before model.
     * <p>
     * model json:contain all custom properties which only effect on this model.(Do not copy the common properties to the json file!).
     * manufacture based json:contain all default properties which will be different by manufacture.
     * (Special case:imx6 platform has 2 different sdk version(ics and kitkat),
     * all imx6 special items should implement both json.
     * non manufacture based json:contain all default properties which are not depend on manufacture.
     *
     * @param context
     */
    private NoteAppConfig(Context context) {
        backendList.add(objectFromDebugModel(context));
        backendList.add(objectFromModel(context));
        backendList.add(objectFromManufactureBasedDefaultConfig(context));
        backendList.add(objectFromNonManufactureBasedDefaultConfig(context));
        backendList.removeAll(Collections.singleton(null));
    }

    static public NoteAppConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            getCurrentDeviceType();
            globalInstance = new NoteAppConfig(context);
        }
        return globalInstance;
    }

    static private void getCurrentDeviceType() {
        String hardware = Build.HARDWARE;
        if (hardware.startsWith(Constant.RK_PREFIX)) {
            currentDeviceType = DeviceType.RK;
            return;
        }
        currentDeviceType = DeviceType.IMX6;
    }

    @Nullable
    private <T> T getData(String dataKey, Class<T> clazz) {
        GObject backend = new GObject();
        for (GObject object : backendList) {
            if (object.hasKey(dataKey)) {
                backend = object;
                break;
            }
        }
        return backend.getBackend().getObject(dataKey, clazz);
    }

    private GObject objectFromManufactureBasedDefaultConfig(Context context) {
        String name = "";
        switch (currentDeviceType) {
            case DeviceType.IMX6:
                name = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.KITKAT) ? Constant.IMX6_KIT_KAT_BASED_CONFIG_NAME :
                        Constant.IMX6_ICS_BASED_CONFIG_NAME;
                break;
            case DeviceType.RK:
                name = Constant.RK3026_BASED_CONFIG_NAME;

                break;
        }
        return objectFromRawResource(context, buildJsonConfigName(name));
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, buildJsonConfigName(Constant.DEBUG_CONFIG_NAME));
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, buildJsonConfigName(name));
    }

    private GObject objectFromNonManufactureBasedDefaultConfig(Context context) {
        return objectFromRawResource(context, buildJsonConfigName(Constant.NON_MANUFACTURE_BASED_CONFIG_NAME));
    }

    private String buildJsonConfigName(String target) {
        return Constant.NOTE_JSON_PREFIX + target;
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
