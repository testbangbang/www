package con.onyx.android.libsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

import con.onyx.android.libsetting.util.CommonUtil;

/**
 * Created by solskjaer49 on 2016/12/6 12:09.
 */

public class SettingConfig {
    static private final String CUSTOM_SETTING_TTS_PACKAGE_NAME_TAG = "setting_tts_package_name";
    static private final String DEFAULT_ANDROID_SETTING_PACKAGE_NAME = "com.android.settings";
    static private final String CUSTOM_SETTING_TTS_CLASS_NAME_TAG = "setting_tts_class_name";
    static private final String DEFAULT_SETTING_TTS_CLASS_NAME = "com.android.settings.TextToSpeechSettings";


    static private String TAG = SettingConfig.class.getSimpleName();
    private static SettingConfig globalInstance;
    static private final boolean useDebugConfig = false;
    private GObject backend;


    private SettingConfig(Context context) {
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

    static public SettingConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new SettingConfig(context);
        }
        return globalInstance;
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

    public Intent getTTSSettingIntent() {
        Intent intent = new Intent();
        String pkgName = DEFAULT_ANDROID_SETTING_PACKAGE_NAME;
        String className = DEFAULT_SETTING_TTS_CLASS_NAME;
        if (backend != null) {
            if (backend.hasKey(CUSTOM_SETTING_TTS_PACKAGE_NAME_TAG)) {
                pkgName = backend.getString(CUSTOM_SETTING_TTS_PACKAGE_NAME_TAG);
            }
            if (backend.hasKey(CUSTOM_SETTING_TTS_CLASS_NAME_TAG)) {
                className = backend.getString(CUSTOM_SETTING_TTS_CLASS_NAME_TAG);
            }
        }
        intent.setClassName(pkgName, className);
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            intent.setClassName(DEFAULT_ANDROID_SETTING_PACKAGE_NAME, "com.android.settings.Settings");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.tts.TextToSpeechSettings");
        }
        return intent;
    }

    public Intent getTimeZoneSettingIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.ZonePicker");
        return intent;
    }

}
