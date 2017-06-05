package com.onyx.android.sdk.data.request.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        if (config.convertToBlackWhite) {
            temp = BitmapUtils.convertToBlackWhite(temp);
        }
        if (config.targetFormat.contains("bmp")) {
            BitmapUtils.saveBitmapToFile(temp, config.targetDir, config.targetPicPathString, true);
        } else if (config.targetFormat.contains("png")) {
            BitmapUtils.savePngToFile(temp, config.targetDir, config.targetPicPathString, true);
        }
    }
}
