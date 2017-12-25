package com.onyx.jdread.reader.data;

import android.graphics.Rect;

import com.onyx.jdread.reader.event.MenuAreaEvent;
import com.onyx.jdread.reader.event.NextPageEvent;
import com.onyx.jdread.reader.event.PrevPageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class AreaFunctionRecognition {
    private static final int WIDTH_AVERAGE_BLOCK = 3;
    private static final int HEIGHT_AVERAGE_BLOCK = 5;

    /**
     *
     *************************************
     *             t Top Area            *
     *              menu                 *
     ************            *************
     *          *            *           *
     * i Area   *            *  j Right  *
     *          *            *   Area    *
     * pageUp   *  t Center  *  pageDown *
     *          *    Area    *           *
     *          *            *           *
     *          * ************           *
     *          *                        *
     *          *                        *
     *          *  j Bottom              *
     *          *    Area                *
     *************************************
     */
    public static void processAreaFunction(int width, int height, int x, int y) {
        Rect tTopArea = new Rect(0, 0, width, height / HEIGHT_AVERAGE_BLOCK);
        if (tTopArea.contains(x, y)) {
            EventBus.getDefault().post(new MenuAreaEvent());
        }

        Rect iArea = new Rect(0, tTopArea.bottom, width / WIDTH_AVERAGE_BLOCK, height);
        if (iArea.contains(x, y)) {
            EventBus.getDefault().post(new PrevPageEvent());
        }

        Rect jRightArea = new Rect(width - width / WIDTH_AVERAGE_BLOCK, tTopArea.bottom, width, height);
        if (jRightArea.contains(x, y)) {
            EventBus.getDefault().post(new NextPageEvent());
        }

        Rect tCenterArea = new Rect(iArea.right, tTopArea.bottom, jRightArea.left, height - height / HEIGHT_AVERAGE_BLOCK);
        if (tCenterArea.contains(x, y)) {
            EventBus.getDefault().post(new MenuAreaEvent());
        }

        Rect jBottomArea = new Rect(iArea.right, tCenterArea.bottom, tCenterArea.right, height);
        if (jBottomArea.contains(x, y)) {
            EventBus.getDefault().post(new NextPageEvent());
        }
    }
}
