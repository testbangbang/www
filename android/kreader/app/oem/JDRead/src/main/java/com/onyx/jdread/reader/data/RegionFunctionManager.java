package com.onyx.jdread.reader.data;

import android.graphics.Rect;

import com.onyx.jdread.reader.actions.BaseAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class RegionFunctionManager {
    static private Map<Rect,BaseAction> regionAction = new HashMap<>();

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
    static  {
        regionAction.put(PrevPageAction.getRegionOne(),new PrevPageAction());
        regionAction.put(NextPageAction.getRegionOne(),new NextPageAction());
        regionAction.put(NextPageAction.getRegionTwo(),new NextPageAction());
        regionAction.put(ShowSettingMenuAction.getRegionOne(),new ShowSettingMenuAction());
        regionAction.put(ShowSettingMenuAction.getRegionTwo(),new ShowSettingMenuAction());
    }

    public static void processRegionFunction(int x, int y) {
        for (Rect rect : regionAction.keySet()){
            if(rect.contains(x,y)){
                regionAction.get(rect).execute(null);
            }
        }
    }
}
