package com.onyx.jdread.setting.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.JDReadApplication;

/**
 * Created by hehai on 18-1-1.
 */

public class ScreenSaversUtil {
    public static void saveScreen(String sourcePicPathString, String targetPicPathString) {
        WindowManager wm = (WindowManager) JDReadApplication.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int fullScreenPhysicalHeight = wm.getDefaultDisplay().getHeight();
        int fullScreenPhysicalWidth = wm.getDefaultDisplay().getWidth();
        String targetFormat = FileUtils.getFileExtension(targetPicPathString);
        String targetDir = "/data/local/assets/images/";
        Bitmap temp = BitmapFactory.decodeFile(sourcePicPathString);
        if (temp == null) {
            return;
        }
        temp = temp.copy(Bitmap.Config.RGB_565, true);
        if (temp == null) {
            return;
        }
        if (temp.getHeight() > temp.getWidth()) {
            temp = BitmapUtils.rotateBmp(temp, -90);
        }
        if ((temp.getWidth() != fullScreenPhysicalHeight) || temp.getHeight() != fullScreenPhysicalWidth) {
            temp = Bitmap.createScaledBitmap(temp, fullScreenPhysicalHeight, fullScreenPhysicalWidth, true);
        }
        temp = BitmapUtils.convertToBlackWhite(temp);
        if (targetFormat.equalsIgnoreCase("bmp")) {
            BitmapUtils.saveBitmapToFile(temp, targetDir, targetPicPathString, true);
        } else if (targetFormat.equalsIgnoreCase("png")) {
            BitmapUtils.savePngToFile(temp, targetDir, targetPicPathString, true);
        }
    }
}
