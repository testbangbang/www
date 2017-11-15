package com.onyx.android.plato.utils;

import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;


/**
 * Created by huxiaomao on 17/6/29.
 */

public class LocalPackageUpdate {
    public static String checkLocalSystemPakage() {
        if(FileUtils.fileExist(OTAManager.LOCAL_PATH_SDCARD)){
            return OTAManager.LOCAL_PATH_SDCARD;
        }
        if(FileUtils.fileExist(OTAManager.LOCAL_PATH_EXTSD)){
            return OTAManager.LOCAL_PATH_EXTSD;
        }
        return null;
    }

    public static String checkLocalApkPakage(final String apkName) {
        String path = DeviceInfoUtil.getExternalStorageDirectory().getAbsolutePath() + File.separator + apkName;
        if(FileUtils.fileExist(path)){
            return path;
        }
        path = DeviceInfoUtil.getRemovableSDCardDirectory().getAbsolutePath() + File.separator + apkName;
        if(FileUtils.fileExist(path)){
            return path;
        }
        return null;
    }
}
