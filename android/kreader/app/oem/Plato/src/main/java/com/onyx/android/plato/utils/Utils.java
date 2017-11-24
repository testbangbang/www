package com.onyx.android.plato.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.onyx.android.plato.common.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by hehai on 17-10-19.
 */

public class Utils {
    public static int formatStorageSize(float storage) {
        int power = 0;
        while (true) {
            if (storage <= 0) {
                return 0;
            } else if (0 < storage && storage <= 2) {
                return 1 << 1;
            } else if (1 << power < storage && storage <= 1 << (power + 1)) {
                return 1 << power + 1;
            } else {
                power++;
            }
        }
    }

    public static File bitmap2File(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), Constants.BITMAP_NAME);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
