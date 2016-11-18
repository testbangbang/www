package com.onyx.kreader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.host.impl.ReaderTextSplitterImpl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class DeviceUtils {

    public static final String TAG = DeviceUtils.class.getSimpleName();

    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";
    private final static String DEFAULT_TOUCH_DEVICE_PATH = "/dev/input/event1";

    public static boolean isRkDevice() {
        return Build.HARDWARE.startsWith("rk");
    }

    public static String getDeviceSerial(Context context) {
        UUID uuid = null;

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number which we store
        // to a prefs file
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            uuid = UUID.randomUUID();
            e.printStackTrace();
        }

        return uuid.toString();
    }

    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density * 160;
    }

    public static String getApplicationFingerprint(Context context) {
        try {
            String name = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            name = name + " (" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode + ")";
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getScreenOrientation(final Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static void setFullScreen(Activity activity, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= 19) {
            if (fullScreen) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
            } else {

            }
            return;
        }

        Intent intent;
        if (fullScreen) {
            intent = new Intent(HIDE_STATUS_BAR_ACTION);
        } else {
            intent = new Intent(SHOW_STATUS_BAR_ACTION);
        }
        activity.sendBroadcast(intent);
    }

    public static int detectTouchDeviceCount() {
        int count = 0;
        final int DEVICE_MAX = 3;
        for(int i = 1; i < DEVICE_MAX; ++i) {
            String path = String.format("/dev/input/event%d", i);
            if (FileUtils.fileExist(path)) {
                ++count;
            }
        }
        return count;
    }

    public static String detectInputDevicePath() {
        final int DEVICE_MAX = 3;
        String last = DEFAULT_TOUCH_DEVICE_PATH;
        for(int i = 1; i < DEVICE_MAX; ++i) {
            String path = String.format("/dev/input/event%d", i);
            if (FileUtils.fileExist(path)) {
                last = path;
            }
        }
        return last;
    }

    public static boolean isFontFile(final String path) {
        return path.toLowerCase(Locale.getDefault()).endsWith(".otf") ||
                path.toLowerCase(Locale.getDefault()).endsWith(".ttf");
    }

    public static List<FontInfo> buildFontItemAdapter(String currentFont, final List<String> preferredFonts) {
        List<FontInfo> fontInfoList = new ArrayList<>();
        FilenameFilter fontFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return isFontFile(filename);
            }
        };

        File flash = EnvironmentUtil.getExternalStorageDirectory();
        File[] fontsFolderList = new File[]{
                new File(flash, "adobe/resources/fonts"),
                new File("/system/fonts")
        };


        TTFUtils utils = new TTFUtils();
        for (File folder : fontsFolderList) {
            File[] fonts = folder.listFiles(fontFilter);
            if (fonts != null) {
                for (File f : fonts) {
                    if (!f.isFile() || f.isHidden()) {
                        continue;
                    }
                    FontInfo fontInfo = new FontInfo();
                    try {
                        utils.parse(f.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String fontName = utils.getFontName();

                    fontInfo.setName(StringUtils.isNullOrEmpty(fontName)
                            ? f.getName() : fontName);
                    fontInfo.setId(f.getName());
                    fontInfo.setTypeface(createTypefaceFromFile(f));
                    if (f.getName().equalsIgnoreCase(currentFont)) {
                        fontInfoList.add(0, fontInfo);
                        continue;
                    }
                    boolean isAlphaWord =  ReaderTextSplitterImpl.isAlpha(fontInfo.getName().charAt(0));
                    if ((preferredFonts != null && (preferredFonts.contains(fontName) || preferredFonts.contains(f.getName())))
                            || !isAlphaWord) {
                        fontInfoList.add(0, fontInfo);
                    } else {
                        fontInfoList.add(fontInfo);
                    }
                }
            }
        }

        return fontInfoList;
    }

    public static Typeface createTypefaceFromFile(final File f) {
        Typeface typeface = Typeface.DEFAULT;
        try {
            typeface = Typeface.createFromFile(f);
        } catch (Exception e) {
            typeface = Typeface.DEFAULT;
        } finally {
            return typeface;
        }
    }
}
