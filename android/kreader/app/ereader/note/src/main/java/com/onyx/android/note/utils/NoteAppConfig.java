package com.onyx.android.note.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.onyx.android.note.BuildConfig;
import com.onyx.android.note.R;
import com.onyx.android.note.activity.onyx.ManagerActivity;
import com.onyx.android.note.activity.onyx.ScribbleActivity;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created with IntelliJ IDEA. User: zhuzeng Date: 5/18/14 Time: 5:21 PM To change this template use
 * File | Settings | File Templates.
 */
public class NoteAppConfig {

    static private String TAG = NoteAppConfig.class.getSimpleName();
    private GObject backend;
    private static NoteAppConfig globalInstance;

    public static final String HOME_ACTIVITY_PKG_NAME = "home_activity_pkg";
    public static final String HOME_ACTIVITY_CLS_NAME = "home_activity_cls";
    public static final String SCRIBBLE_ACTIVITY_PKG_NAME = "scribble_activity_pkg";
    public static final String SCRIBBLE_ACTIVITY_CLS_NAME = "scribble_activity_cls";
    public static final String USE_FULL_SCREEN = "full_screen";
    public static final String USE_LINE_LAYOUT = "use_line_layout";
    public static final String SHOW_INPUT_METHOD_INSTANTLY_AFTER_OPEN_DIALOG = "show_input_method_instantly_after_open_dialog";
    public static final String USE_MX_UI_STYLE = "use_mx_ui_style";
    //once use edu config.hide import/export function.give extra shape and less note background.
    public static final String USE_EDU_CONFIG = "use_edu_config";
    static public final boolean useDebugConfig = false;

    static public NoteAppConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new NoteAppConfig(context);
        }
        return globalInstance;
    }

    public final boolean useMXUIStyle() {
        return backend.hasKey(USE_MX_UI_STYLE) && backend.getBoolean(USE_MX_UI_STYLE, false);
    }

    public final int getFolderIconRes() {
        return backend.getBoolean(USE_MX_UI_STYLE, false) ?
                R.drawable.ic_student_note_folder_gray : R.drawable.directory;
    }

    //show input method instantly after dialog open ,default value is true.
    public final boolean showInputMethodInstantlyAfterOpenDialog() {
        return !backend.hasKey(SHOW_INPUT_METHOD_INSTANTLY_AFTER_OPEN_DIALOG)
                || backend.getBoolean(SHOW_INPUT_METHOD_INSTANTLY_AFTER_OPEN_DIALOG, true);
    }

    public final String getHomeActivityPackageName() {
        if (backend.hasKey(HOME_ACTIVITY_PKG_NAME)) {
            return backend.getString(HOME_ACTIVITY_PKG_NAME);
        }
        return null;
    }

    public final String getHomeActivityClassName() {
        if (backend.hasKey(HOME_ACTIVITY_CLS_NAME)) {
            return backend.getString(HOME_ACTIVITY_CLS_NAME);
        }
        return null;
    }

    public final String getScribbleActivityPackageName() {
        if (backend.hasKey(SCRIBBLE_ACTIVITY_PKG_NAME)) {
            return backend.getString(SCRIBBLE_ACTIVITY_PKG_NAME);
        }
        return null;
    }

    public final String getScribbleActivityClassName() {
        if (backend.hasKey(SCRIBBLE_ACTIVITY_CLS_NAME)) {
            return backend.getString(SCRIBBLE_ACTIVITY_CLS_NAME);
        }
        return null;
    }

    public boolean useFullScreen() {
        return backend.hasKey(USE_FULL_SCREEN) && backend.getBoolean(USE_FULL_SCREEN);
    }

    public boolean useLineLayout() {
        return backend.hasKey(USE_LINE_LAYOUT) && backend.getBoolean(USE_LINE_LAYOUT);
    }

    private NoteAppConfig(Context context) {
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

        backend = objectFromModel(context);
        if (backend != null) {
            Log.i(TAG, "Using device model.");
            return;
        }

        Log.i(TAG, "Using default model.");
        backend = objectFromDefaultOnyxConfig(context);
    }

    private GObject objectFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
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

    public Intent getNoteHomeIntent(Context context) {
        if (StringUtils.isNotBlank(getHomeActivityClassName()) && StringUtils.isNotBlank(getHomeActivityPackageName())) {
            return ActivityUtil.createIntent(getHomeActivityPackageName(), getHomeActivityClassName());
        } else {
            return new Intent(context, ManagerActivity.class);
        }
    }

    public Intent getScribbleIntent(Context context) {
        if (StringUtils.isNotBlank(getScribbleActivityPackageName()) && StringUtils.isNotBlank(getScribbleActivityClassName())) {
            return ActivityUtil.createIntent(getScribbleActivityPackageName(), getScribbleActivityClassName());
        } else {
            return new Intent(context, ScribbleActivity.class);
        }
    }

    //TODO:key to config support Export or not,now just disable for no function.
    public boolean isEnableExport() {
        return false;
    }

    //TODO:key to config support PressStress or not,now just disable for no function.
    public boolean isEnablePressStressDetect(){
        return false;
    }

    public boolean useEduConfig(){
        return backend.hasKey(USE_EDU_CONFIG) && backend.getBoolean(USE_EDU_CONFIG);
    }
}
