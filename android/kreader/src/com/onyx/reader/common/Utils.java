package com.onyx.reader.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

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
}
