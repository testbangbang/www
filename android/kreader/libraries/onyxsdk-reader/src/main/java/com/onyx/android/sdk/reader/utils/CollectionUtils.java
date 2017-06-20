package com.onyx.android.sdk.reader.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class CollectionUtils {

    static public boolean isNullOrEmpty(final Collection list) {
        return list == null || list.size() <= 0;
    }

    static public boolean isNullOrEmpty(final Map map) {
        return map == null || map.size() <= 0;
    }

    static public boolean contains(final Set<String> set, final String value) {
        if (set != null) {
            return set.contains(value);
        }
        return true;
    }

    static public boolean contains(final Set<String> source, final Collection<String> target) {
        if (source == null && target == null) {
            return true;
        }
        if (source == null || target == null) {
            return false;
        }
        for(String string : target) {
            if (source.contains(string)) {
                return true;
            }
        }
        return false;
    }


}
