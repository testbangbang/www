package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.NextPageEvent;

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
 *          *            *  J Right  *
 *          *            *   region  *
 *          *            *  pageDown *
 *          *            *           *
 *          *            *           *
 *          * ************           *
 *          *                        *
 *          *                        *
 *          *  J Bottom              *
 *          *    region              *
 *************************************
 */
public class JRegionFunctionAction extends RegionBaseAction {
    public JRegionFunctionAction(int width, int height, int x, int y) {
        super(width, height, x, y);
        setRegion();
    }

    @Override
    public void setRegion() {
        Rect jRightArea = new Rect(getWidth() - getWidth() / WIDTH_AVERAGE_BLOCK, getHeight() / HEIGHT_AVERAGE_BLOCK, getWidth(), getHeight());
        addRect(jRightArea);
        Rect jBottomArea = new Rect(getWidth() / WIDTH_AVERAGE_BLOCK, getHeight() - getHeight() / HEIGHT_AVERAGE_BLOCK, getWidth() - getWidth() / WIDTH_AVERAGE_BLOCK, getHeight());
        addRect(jBottomArea);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        EventBus.getDefault().post(new NextPageEvent());
    }
}
