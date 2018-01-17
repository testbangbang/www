package com.onyx.jdread.reader.data;

import android.content.Context;
import android.graphics.Rect;

import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class RegionFunctionManager {
    static private Map<Rect,BaseReaderAction> regionAction = new HashMap<>();

    /**
     *
     *************************************
     *             t Top region          *
     *              menu                 *
     ************            *************
     *          *            *           *
     * i region *            *  j Right  *
     *          *            *   region  *
     * pageUp   *  t Center  *  pageDown *
     *          *    region  *           *
     *          *            *           *
     *          * ************           *
     *          *                        *
     *          *                        *
     *          *  j Bottom              *
     *          *    region              *
     *************************************
     */
    public static void initRegionAction(Context context) {
        regionAction.put(PrevPageAction.getRegionOne(context), new PrevPageAction());
        regionAction.put(NextPageAction.getRegionOne(context), new NextPageAction());
        regionAction.put(NextPageAction.getRegionTwo(context), new NextPageAction());
        regionAction.put(ShowSettingMenuAction.getRegionOne(context), new ShowSettingMenuAction());
        regionAction.put(ShowSettingMenuAction.getRegionTwo(context), new ShowSettingMenuAction());
    }

    public static boolean processRegionFunction(ReaderDataHolder readerDataHolder, int x, int y) {
        for (Rect rect : regionAction.keySet()) {
            if (rect.contains(x, y)) {
                regionAction.get(rect).execute(readerDataHolder);
                return true;
            }
        }
        return false;
    }
}
