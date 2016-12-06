package con.onyx.android.libsetting.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/11/29 12:25.
 */

public class SettingCategory {
    public static final int Wifi = 0;
    public static final int BLUETOOTH = 1;
    public static final int SOUND = 2;
    public static final int STORAGE = 3;
    public static final int LANGUAGE_AND_INPUTMETHOD = 4;
    public static final int DATE = 5;
    public static final int APPLICATION = 6;
    public static final int OTHER = 7;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({Wifi, BLUETOOTH, SOUND, STORAGE, LANGUAGE_AND_INPUTMETHOD, DATE, APPLICATION, OTHER})
    // Create an interface for validating int types
    public @interface SettingCategoryDef {
    }

    public
    @SettingCategoryDef
    static int translate(int val) {
        return val;
    }
}
