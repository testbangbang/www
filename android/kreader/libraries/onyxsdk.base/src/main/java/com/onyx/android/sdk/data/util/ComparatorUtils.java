package com.onyx.android.sdk.data.util;

import com.onyx.android.sdk.data.AscDescOrder;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by solskjaer49 on 14/10/23 15:24.
 */
public class ComparatorUtils {

    public static int stringComparator(String lhs, String rhs, AscDescOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return Collator.getInstance(Locale.getDefault()).compare(
                        rhs, lhs);
            default:
                return Collator.getInstance(Locale.getDefault()).compare(
                        lhs, rhs);
        }
    }

    public static int longComparator(long lhs, long rhs, AscDescOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return rhs < lhs ? -1 : (lhs == rhs ? 0 : 1);
            default:
                return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

    public static int integerComparator(int lhs, int rhs, AscDescOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return rhs < lhs ? -1 : (lhs == rhs ? 0 : 1);
            default:
                return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

    public static int booleanComparator(boolean lhs, boolean rhs, AscDescOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return lhs == rhs ? 0 : rhs ? 1 : -1;
            default:
                return lhs == rhs ? 0 : lhs ? 1 : -1;
        }
    }
}
