package com.onyx.jdread.reader.data;

import com.onyx.jdread.reader.actions.IRegionFunctionAction;
import com.onyx.jdread.reader.actions.JRegionFunctionAction;
import com.onyx.jdread.reader.actions.TRegionFunctionAction;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class RegionFunctionRecognition {


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
    public static void processRegionFunction(int width, int height, int x, int y) {
        TRegionFunctionAction tRegionFunctionAction = new TRegionFunctionAction(width,height,x,y);
        if(tRegionFunctionAction.isContains()){
            tRegionFunctionAction.execute(null);
            return;
        }
        IRegionFunctionAction iRegionFunctionAction = new IRegionFunctionAction(width,height,x,y);
        if(iRegionFunctionAction.isContains()){
            iRegionFunctionAction.execute(null);
            return;
        }
        JRegionFunctionAction jRegionFunctionAction = new JRegionFunctionAction(width,height,x,y);
        if(jRegionFunctionAction.isContains()){
            jRegionFunctionAction.execute(null);
            return;
        }
    }
}
