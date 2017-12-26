package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PrevPageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/26.
 */
/**
 *
 *************************************
 *                                   *
 *                                   *
 ************            *************
 *          *            *           *
 *          *            *           *
 *          *            *           *
 * I region *            *           *
 *          *            *           *
 *          *            *           *
 *  pageUp  * ************           *
 *          *                        *
 *          *                        *
 *          *                        *
 *          *                        *
 *************************************
 */
public class IRegionFunctionAction extends RegionBaseAction {
    public IRegionFunctionAction(int width, int height, int x, int y) {
        super(width, height, x, y);
        setRegion();
    }

    @Override
    public void setRegion() {
        Rect iArea = new Rect(0, getHeight() / HEIGHT_AVERAGE_BLOCK, getWidth() / WIDTH_AVERAGE_BLOCK, getHeight());
        addRect(iArea);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        EventBus.getDefault().post(new PrevPageEvent());
    }
}
