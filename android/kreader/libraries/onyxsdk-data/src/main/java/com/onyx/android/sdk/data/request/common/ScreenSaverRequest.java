package com.onyx.android.sdk.data.request.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.common.ScreenSaverConfig;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by suicheng on 2017/5/26.
 */
public class ScreenSaverRequest extends BaseDataRequest {

    private ScreenSaverConfig config;

    public ScreenSaverRequest(ScreenSaverConfig saverConfig) {
        this.config = saverConfig;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        saveScreenFile(config);
    }

    private void saveScreenFile(ScreenSaverConfig config) throws Exception {
        int fullScreenPhysicalHeight = config.fullScreenPhysicalHeight;
        int fullScreenPhysicalWidth = config.fullScreenPhysicalWidth;
        Bitmap temp = BitmapFactory.decodeFile(config.sourcePicPathString).copy(Bitmap.Config.RGB_565, true);
        if (temp.getHeight() > temp.getWidth()) {
            temp = BitmapUtils.rotateBmp(temp, config.picRotateDegrees);
        }
        if ((temp.getWidth() != fullScreenPhysicalHeight) ||
                temp.getHeight() != fullScreenPhysicalWidth) {
            temp = Bitmap.createScaledBitmap(temp, fullScreenPhysicalHeight, fullScreenPhysicalWidth, true);
        }
        if (config.convertToGrayScale) {
            temp = BitmapUtils.convertToBlackWhite(temp);
        }
        boolean success = false;
        if (config.targetFormat.contains("bmp")) {
            success = BitmapUtils.saveBitmapToFile(temp, config.targetDir, config.targetPicPathString, true);
        } else if (config.targetFormat.contains("png")) {
            success = BitmapUtils.savePngToFile(temp, config.targetDir, config.targetPicPathString, true);
        }
        if (success) {
            Log.i("screenSaver", "success");
            Intent intent = new Intent("update_standby_pic");
            getContext().sendBroadcast(intent);
        }
    }
}
