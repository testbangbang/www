package con.onyx.android.libsetting.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/11/29 12:25.
 */

public class SettingCategory {
    public static final int NETWORK = 0;
    public static final int USER_SETTING = 1;
    public static final int SOUND = 2;
    public static final int STORAGE = 3;
    public static final int LANGUAGE_AND_DATE = 4;
    public static final int APPLICATION_MANAGEMENT = 5;
    public static final int POWER = 6;
    public static final int SECURITY = 7;
    public static final int ERROR_REPORT = 8;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({NETWORK, USER_SETTING, SOUND, STORAGE,
            LANGUAGE_AND_DATE, APPLICATION_MANAGEMENT, POWER, SECURITY, ERROR_REPORT})
    // Create an interface for validating int types
    public @interface SettingCategoryDef {
    }

    public
    @SettingCategoryDef
    static int translate(int val) {
        return val;
    }
}
