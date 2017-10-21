package com.onyx.android.sun.utils;

/**
 * Created by hehai on 17-10-19.
 */

public class Utils {
    public static int formatStorageSize(float storage) {
        int power = 0;
        while (true) {
            if (storage <= 0) {
                return 0;
            } else if (0 < storage && storage <= 2) {
                return 1 << 1;
            } else if (1 << power < storage && storage <= 1 << (power + 1)) {
                return 1 << power + 1;
            } else {
                power++;
            }
        }
    }
}
