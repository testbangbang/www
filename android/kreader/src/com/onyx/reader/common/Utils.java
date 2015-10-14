package com.onyx.reader.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class Utils {

    public static long nanoTime() {
        return System.nanoTime();
    }

    static public boolean fileExist(final String path) {
        File file = new File(path);
        return file.exists();
    }

    static public Bitmap loadBitmapFromFile(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (!Utils.fileExist(path)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    static public boolean saveBitmap(Bitmap bitmap, final String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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


}
