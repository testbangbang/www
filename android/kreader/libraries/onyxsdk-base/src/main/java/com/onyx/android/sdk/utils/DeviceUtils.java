package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.device.Device;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by ming on 2016/12/3.
 */

public class DeviceUtils {
    public static final String TAG = DeviceUtils.class.getSimpleName();

    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    private final static String DEFAULT_TOUCH_DEVICE_PATH = "/dev/input/event1";
    public static final int NEVER_SLEEP = Integer.MAX_VALUE;

    public static boolean isRk32xxDevice() {
        return getSystemProperty("ro.board.platform").contains("3288");
    }

    public static boolean isRkDevice() {
        return Build.HARDWARE.startsWith("rk");
    }

    public enum FontType {
        CHINESE,ENGLISH,ALL
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

    public static float getDensityDPI(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.densityDpi;
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

    public static int getScreenOrientation(final Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
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
        String last = DEFAULT_TOUCH_DEVICE_PATH;
        String index = DetectInputDeviceUtil.detectInputDevicePath();
        if (StringUtils.isNotBlank(index)) {
            last = String.format("/dev/input/event%s", index);
        }
        return last;
    }

    public static boolean isFontFile(final String path) {
        return path.toLowerCase(Locale.getDefault()).endsWith(".otf") ||
                path.toLowerCase(Locale.getDefault()).endsWith(".ttf");
    }

    public static List<FontInfo> buildFontItemAdapter(List<String> fontsFolderList, String currentFont, final List<String> preferredFonts, final FontType fontType) {
        List<FontInfo> fontInfoList = new ArrayList<>();
        FilenameFilter fontFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return isFontFile(filename);
            }
        };

        TTFUtils utils = new TTFUtils();
        for (String path : fontsFolderList) {
            File folder = new File(path);
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
                    fontInfo.setId(f.getAbsolutePath());
                    fontInfo.setTypeface(createTypefaceFromFile(f));
                    boolean isAlphaWord =  StringUtils.isAlpha(fontInfo.getName().charAt(0));
                    if (fontType == FontType.ENGLISH && !isAlphaWord) {
                        continue;
                    }
                    if (fontType == FontType.CHINESE && isAlphaWord) {
                        continue;
                    }
                    if (f.getName().equalsIgnoreCase(currentFont)) {
                        fontInfoList.add(0, fontInfo);
                        continue;
                    }
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

    public static int getBatteryPercentLevel(final Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level / (float)scale;

        return (int)(batteryPercent*100);
    }

    public static boolean isEngVersion() {
        final String type = Build.TYPE;
        final String display = Build.DISPLAY;
        final String finger = Build.FINGERPRINT;
        final String tag = "eng";
        return safeContains(type, tag) || safeContains(display, tag) || safeContains(finger, tag);
    }

    public static boolean safeContains(final String value, final String tag) {
        if (StringUtils.isNotBlank(value) && value.toLowerCase().contains(tag)) {
            return true;
        }
        return false;
    }

    public static void setFullScreenOnResume(Activity activity, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= 19) {
            adjustFullScreenStatusForAPIAbove19(activity,fullScreen);
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

    static void adjustFullScreenStatusForAPIAbove19(final Activity activity, boolean fullScreen) {
        adjustFullScreenStatus(activity.getWindow(), fullScreen);
    }

    public static void adjustDialogFullScreenStatusForAPIAbove19(Dialog dialog, boolean fullScreen) {
        if (Build.VERSION.SDK_INT <= 19) {
            return;
        }
        adjustFullScreenStatus(dialog.getWindow(), fullScreen);
    }

    public static void adjustFullScreenStatus(Window window, boolean fullScreen) {
        int clearFlag, targetFlag, uiOption;
        if (fullScreen) {
            clearFlag = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            targetFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        } else {
            clearFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            targetFlag = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            uiOption = View.SYSTEM_UI_FLAG_VISIBLE;
        }
        window.clearFlags(clearFlag);
        window.setFlags(targetFlag, targetFlag);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(uiOption);
    }

    public static void setFullScreenOnCreate(final Activity activity, boolean fullScreen) {
        if (fullScreen) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public static boolean isFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            int flag = activity.getWindow().getAttributes().flags;
            return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

        int[] location = new int[2];
        activity.getWindow().getDecorView().getLocationOnScreen(location);
        return location[1] <= 0;
    }

    /**
     * Check if fullscreen is activated by a position of a top left View
     * @param topLeftView View which position will be compared with 0,0
     * @return
     */
    public static boolean isFullScreen(View topLeftView) {
        int location[] = new int[2];
        topLeftView.getLocationOnScreen(location);
        return location[0] == 0 && location[1] == 0;
    }

    public static boolean isDeviceInteractive(Context context) {
        PowerManager powerManager = (PowerManager)context.getSystemService(POWER_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH
                ? powerManager.isInteractive()
                : powerManager.isScreenOn();
    }

    public static boolean isChinese(final Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    static public void turnOffSystemPMSettings(Context context) {
        android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, NEVER_SLEEP);
        android.provider.Settings.System.putInt(context.getContentResolver(), "auto_poweroff_timeout", NEVER_SLEEP);
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getPackageVersionName(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(
                getPackageName(context), 0).versionName;
    }

    public static int getPackageVersionCode(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(
                getPackageName(context), 0).versionCode;
    }

    public static String getApplicationName(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(context), 0);
        return packageManager.getApplicationLabel(applicationInfo).toString();
    }

    public static String getSystemProperty(String key) {
        String value = "";
        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}
