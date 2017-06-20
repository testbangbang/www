package com.onyx.android.sdk.data.model;

import android.os.Build;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.utils.FirmwareUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by zhuzeng on 8/20/15.
 */
public class Firmware extends BaseData {

    public static final String BUILD_NUMBER_TAG = "buildNumber";
    public static final String RELEASE_TAG = "release";
    public static final String TESTING_TAG = "test";


    public int buildNumber;         // build number from build server.
    public String buildType;        // user and eng.
    public String fingerprint;      // post by admin
    public String buildDisplayId;   // show to end user, not for query.
    public String lang;             // the pkg language
    public String model;            // post by admin
    public String brand;            // post by admin
    public String fwType;           // firmware type, testing or release.
    public String deviceMAC;        // device mac
    public String md5;
    public int widthPixels, heightPixels;

    public List<String> changeList;
    public List<String> downloadUrlList;

    public static Firmware currentFirmware() {
        Firmware fw = new Firmware();
        fw.model = Build.MODEL;
        fw.fingerprint = Build.FINGERPRINT;
        fw.updateReleaseBuildParameters();
        return fw;
    }

    public void updateReleaseBuildParameters() {
        buildNumber = FirmwareUtils.getBuildIdFromFingerprint(fingerprint);
        buildType = FirmwareUtils.getBuildTypeFromFingerprint(fingerprint);
        fwType = RELEASE_TAG;
    }

    public void updateTestingBuildParameters() {
        buildNumber = FirmwareUtils.getBuildIdFromFingerprint(fingerprint);
        buildType = FirmwareUtils.getBuildTypeFromFingerprint(fingerprint);
        fwType = TESTING_TAG;
    }

    @JSONField(serialize=false)
    public String getChangeLog() {
        if (!CollectionUtils.isNullOrEmpty(changeList)) {
            return StringUtils.join(changeList, "\n");
        }
        return "";
    }

    public String getFingerprint() {
        return fingerprint;
    }

    @JSONField(serialize=false)
    public String getUrl() {
        if (downloadUrlList.size() > 0) {
            return downloadUrlList.get(0);
        }
        return null;
    }
}
