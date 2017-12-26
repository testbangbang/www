package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.MenuAreaEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/26.
 */
/**
 *
 *************************************
 *             T Top region          *
 *              menu                 *
 ************            *************
 *          *            *           *
 *          *            *           *
 *          *            *           *
 *          *  T Center  *           *
 *          *    region  *           *
 *          *            *           *
 *          * ************           *
 *          *                        *
 *          *                        *
 *          *                        *
 *          *                        *
 *************************************
 */
public class TRegionFunctionAction extends RegionBaseAction {
    public TRegionFunctionAction(int width, int height, int x, int y) {
        super(width, height, x, y);
        setRegion();
    }

    @Override
    public void setRegion(){
        Rect tTopArea = new Rect(0, 0, getWidth(), getHeight() / HEIGHT_AVERAGE_BLOCK);
        addRect(tTopArea);
        Rect tCenterArea = new Rect(getWidth() / WIDTH_AVERAGE_BLOCK, tTopArea.bottom, getWidth() - getWidth() / WIDTH_AVERAGE_BLOCK, getHeight() - getHeight() / HEIGHT_AVERAGE_BLOCK);
        addRect(tCenterArea);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        EventBus.getDefault().post(new MenuAreaEvent());
    }
}
