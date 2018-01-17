package com.onyx.jdread.setting.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hehai on 18-1-1.
 */

public class Constants {
    public static final String STANDBY_PIC_DIRECTORY = "/data/local/assets/images/";
    public static final String STANDBY_PIC_NAME = "standby-1.png";
    public static int PASSWORD_MIN_LENGTH = 4;
    public static int PASSWORD_MAX_LENGTH = 12;

    public static Set<String> dictionaryPackageSet = new HashSet<>();

    static {
        dictionaryPackageSet.add("com.kingsoft");
    }
}
