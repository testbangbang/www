package com.onyx.android.cropimage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.alibaba.fastjson.JSON;
import com.onyx.android.cropimage.data.CropArgs;

public class CropImageResultReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(CropImage.INTENT_ACTION_SELECT_ZOOM_RECT)) {
            CropArgs args = JSON.parseObject(intent.getStringExtra(CropImage.CROP_ARGS), CropArgs.class);
            onSelectionFinished(args);
        }
    }

    public void onSelectionFinished(final CropArgs args) {
    }
}