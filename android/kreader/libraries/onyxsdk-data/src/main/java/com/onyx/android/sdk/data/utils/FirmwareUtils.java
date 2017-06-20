package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 8/20/15.
 */
public class FirmwareUtils {

        /*
        * MacCentre/MC_C67ML/MC_C67ML:4.2.2/JDQ_1504221659/525:user/release-keys
        * Artatech/M96Universe/M96Universe:4.0.4/2014-09-12_00-43_1.6.2_041be89/506:user/release-keys
        * ONYX/M96/M96:4.0.4/2099-07-17_12-24-08/654:eng/release-keys
        */
        private static String getBuildEntryFromFingerprint(final String fingerprint) {
            String entries[] = fingerprint.split("/");
            if (entries.length < 2) {
                return null;
            }
            String key = entries[entries.length - 2];
            if (StringUtils.isNullOrEmpty(key)) {
                return null;
            }
            return key;
        }

        public static int getBuildIdFromFingerprint(final String fingerprint) {
            final String key = getBuildEntryFromFingerprint(fingerprint);
            if (StringUtils.isNullOrEmpty(key)) {
                return -1;
            }
            String items[] = key.split(":");
            if (items.length < 2) {
                return -1;
            }
            int value = Integer.valueOf(items[0]);
            return value;
        }

        public static String getBuildTypeFromFingerprint(final String fingerprint) {
            final String key = getBuildEntryFromFingerprint(fingerprint);
            if (StringUtils.isNullOrEmpty(key)) {
                return null;
            }
            String items[] = key.split(":");
            if (items.length < 2) {
                return null;
            }
            return items[1];
        }

        // ro.build.display.id=1.6.6-mc 2015-04-22_16-51 e8174a5
        public static String getBuildDateFromBuildDisplayId(final String buildDisplayId) {
            String entries[] = buildDisplayId.split(" ");
            if (entries.length < 3) {
                return null;
            }
            final String key = entries[entries.length - 2];
            if (StringUtils.isNullOrEmpty(key)) {
                return null;
            }
            return key;
        }

}
