package com.onyx.android.sdk.reader.api;

import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.List;

/**
 * Created by zhuzeng on 10/4/15.
 */
public interface ReaderPluginOptions {

    AssetManager getAssetManager();

    Resources getResources();

    List<String> getFontDirectories();

    float getScreenDensity();


}
