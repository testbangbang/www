package com.onyx.android.plato.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.onyx.android.plato.SunApplication;
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

    public static void loadImageUrl(String url, ImageView imageView, int defaultImage) {
        Glide.with(SunApplication.getInstance()).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImage).into(imageView);
    }

    public static void hideSoftWindow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) SunApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}
