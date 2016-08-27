package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and function provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk.
 */
public class DataProviderManager {

    public static final String TAG = DataProviderManager.class.getSimpleName();
    private static DataProviderBase instance;

    public static DataProviderBase getDataProvider() {
        if (instance == null) {
            instance = new LocalDataProvider();
        }
        return instance;
    }



}
