package com.onyx.android.cropimage;

import com.onyx.android.cropimage.data.CropArgs;

public abstract class CropImageResultCallback {

    public abstract void onSelectionFinished(final CropArgs args);
}