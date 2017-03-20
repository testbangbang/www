package com.onyx.android.sdk.utils;


import java.io.File;

/**
 * Created by wangxu on 17-3-18.
 */

public class DetectInputDeviceUtil {

    private static final String INPUT_DEVICES_INFO_FILE = "/proc/bus/input/devices";
    private static final String HANVON_TP_NAME = "hanvon_tp";
    private static final String SPLIT_TAG_B = "B:";
    private static final String MATCH_TAG_HANDLERS = "Handlers";

    public static String detectInputDevicePath() {
        String result = null;
        if (FileUtils.fileExist(INPUT_DEVICES_INFO_FILE)) {
            String content = FileUtils.readContentOfFile(new File(INPUT_DEVICES_INFO_FILE));
            if (StringUtils.isNotBlank(content)) {
                String[] splitBstr = split(content, SPLIT_TAG_B);
                if (null != splitBstr) {
                    String matchTpStr = match(splitBstr, HANVON_TP_NAME);
                    if (StringUtils.isNotBlank(matchTpStr)) {
                        String[] splitStr = split(matchTpStr, " ");
                        if (null != splitStr) {
                            String matchStr = match(splitStr, MATCH_TAG_HANDLERS);
                            if (StringUtils.isNotBlank(matchStr)) {
                                result = matchStr.substring(matchStr.length() -1);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static String[] split(final String content, final String regex) {
        if (StringUtils.isNullOrEmpty(content) || null == regex) {
            return null;
        }
        return content.split(regex);
    }

    private static String match(final String[] content, final String str) {
        if (content == null || StringUtils.isNullOrEmpty(str)) {
            return null;
        }
        String result = null;
        for (String s : content) {
            if (s.contains(str)) {
                result = s;
                break;
            }
        }
        return result;
    }
}
