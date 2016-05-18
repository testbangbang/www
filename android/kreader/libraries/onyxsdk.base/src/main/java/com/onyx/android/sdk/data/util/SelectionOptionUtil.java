package com.onyx.android.sdk.data.util;

import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.SelectionOption;

import java.util.HashSet;

/**
 * Created by joy on 4/25/14.
 */
public final class SelectionOptionUtil {
    /**
     *
     * @param item
     * @param option
     * @return true if selection changed, false if not
     */
    public static boolean toggleSelection(GObject item, SelectionOption option) {
        boolean selected = item.getBoolean(GAdapterUtil.TAG_SELECTED);
        if (selected) {
            if (option.mustSelectAtLeastOne() && option.getSelections().size() <= 1) {
                return false;
            } else {
                item.putBoolean(GAdapterUtil.TAG_SELECTED, false);
                option.getSelections().remove(item);
            }
        } else {
            if (!option.canSelectMultiple() && option.getSelections().size() > 0) {
                for (Object o : option.getSelections()) {
                    GObject g = (GObject)o;
                    g.putBoolean(GAdapterUtil.TAG_SELECTED, false);
                }
                option.getSelections().clear();
            }
            item.putBoolean(GAdapterUtil.TAG_SELECTED, true);
            ((HashSet<GObject>)option.getSelections()).add(item);
        }

        return true;
    }
}
