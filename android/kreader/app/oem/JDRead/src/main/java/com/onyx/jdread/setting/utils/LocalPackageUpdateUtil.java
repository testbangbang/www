package com.onyx.jdread.setting.utils;

import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.utils.FileUtils;

/**
 * Created by li on 2017/12/25.
 */

public class LocalPackageUpdateUtil {

    public static String checkLocalPackage() {
        String path = null;
        if (FileUtils.fileExist(OTAManager.LOCAL_PATH_SDCARD)) {
            path = OTAManager.LOCAL_PATH_SDCARD;
        }

        if (FileUtils.fileExist(OTAManager.LOCAL_PATH_EXTSD)) {
            path = OTAManager.LOCAL_PATH_EXTSD;
        }
        return path;
    }
}
