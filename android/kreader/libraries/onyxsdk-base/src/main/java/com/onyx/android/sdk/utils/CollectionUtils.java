package com.onyx.android.sdk.utils;

import com.onyx.android.sdk.data.GAdapter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhuzeng on 12/9/15.
 */
public class CollectionUtils {

    static public boolean isNullOrEmpty(final Collection list) {
        return list == null || list.size() <= 0;
    }

    static public boolean isNullOrEmpty(final Map map) {
        return map == null || map.size() <= 0;
    }

    static public boolean isNullOrEmpty(final GAdapter adapter) {
        return adapter == null || isNullOrEmpty(adapter.getList());
    }

    static public boolean contains(final Set<String> set, final String value) {
        if (set != null && set.size() > 0) {
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
        for (String string : target) {
            if (source.contains(string)) {
                return true;
            }
        }
        return false;
    }

    static public boolean equals(final Set firstSet, final Set secondSet) {
        if (firstSet == null || secondSet == null) {
            return false;
        }
        return firstSet.equals(secondSet);
    }

}
