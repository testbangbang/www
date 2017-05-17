/*
 * Copyright (C) 2016 eschao <esc.chao@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onyx.phone.reader.reader.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.eschao.android.widget.pageflip.OnPageFlipListener;
import com.eschao.android.widget.pageflip.PageFlip;

/**
 * Abstract Page Render
 *
 * @author eschao
 */

public abstract class PageRender implements OnPageFlipListener {

    public final static int MSG_ENDED_DRAWING_FRAME = 1;
    private final static String TAG = "PageRender";

    final static int DRAW_MOVING_FRAME = 0;
    final static int DRAW_ANIMATING_FRAME = 1;
    final static int DRAW_FULL_PAGE = 2;

    int pageNo;
    int pageCount;
    int drawCommand;
    boolean forward;
    Canvas canvas;
    Context context;
    PageFlip pageFlip;
    SparseArray<Bitmap> pageMaps;

    public PageRender(Context context, PageFlip pageFlip, int pageNo) {
        this.context = context;
        this.pageFlip = pageFlip;
        this.pageNo = pageNo;
        drawCommand = DRAW_FULL_PAGE;
        canvas = new Canvas();
        this.pageFlip.setListener(this);
        pageMaps = new SparseArray<>();
    }

    public void updatePage(Bitmap page, int position) {
        pageMaps.put(position, page);
    }

    public Bitmap getPageView(final int position) {
        return pageMaps.get(position);
    }

    /**
     * Get page number
     *
     * @return page number
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * Release resources
     */
    public void release() {
        pageFlip.setListener(null);
        canvas = null;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Handle finger moving event
     *
     * @param x x coordinate of finger moving
     * @param y y coordinate of finger moving
     * @return true if event is handled
     */
    public boolean onFingerMove(float x, float y) {
        drawCommand = DRAW_MOVING_FRAME;
        return true;
    }

    /**
     * Handle finger up event
     *
     * @param x x coordinate of finger up
     * @param y y coordinate of inger up
     * @return true if event is handled
     */
    public boolean onFingerUp(float x, float y) {
        if (pageFlip.animating()) {
            drawCommand = DRAW_ANIMATING_FRAME;
            return true;
        }

        return false;
    }

    /**
     * Calculate font size by given SP unit
     */
    protected int calcFontSize(int size) {
        return (int)(size * context.getResources().getDisplayMetrics()
                                    .scaledDensity);
    }

    /**
     * Render page frame
     */
    abstract void onDrawFrame();

    /**
     * Handle surface changing event
     *
     * @param width surface width
     * @param height surface height
     */
    abstract void onSurfaceChanged(int width, int height);

    /**
     * Handle drawing ended event
     *
     * @param what draw command
     * @return true if render is needed
     */
    abstract boolean onEndedDrawing(int what);
}
