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
import android.os.Handler;

import com.eschao.android.widget.pageflip.Page;
import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipState;

/**
 * Single page render
 * <p>
 * Every page need 2 texture in single page mode:
 * <ul>
 *     <li>First texture: current page content</li>
 *     <li>Back texture: back of front content, it is same with first texture
 *     </li>
 *     <li>Second texture: next page content</li>
 * </ul>
 * </p>
 *
 * @author eschao
 */

public class SinglePageRender extends PageRender {

    /**
     * Constructor
     * @see {@link #(Context, PageFlip, Handler, int)}
     */
    public SinglePageRender(Context context, PageFlip pageFlip, int pageNo) {
        super(context, pageFlip, pageNo);
    }

    /**
     * Draw frame
     */
    public void onDrawFrame() {
        // 1. delete unused textures
        pageFlip.deleteUnusedTextures();
        Page page = pageFlip.getFirstPage();
        forward = pageFlip.getFlipState() == PageFlipState.FORWARD_FLIP;

        // 2. handle drawing command triggered from finger moving and animating
        if (drawCommand == DRAW_MOVING_FRAME ||
            drawCommand == DRAW_ANIMATING_FRAME) {
            // is forward flip
            if (pageFlip.getFlipState() == PageFlipState.FORWARD_FLIP) {
                // check if second texture of first page is valid, if not,
                // create new one
                if (!page.isSecondTextureSet()) {
                    Bitmap secondPage = getPageView(pageNo + 1);
                    if (!isRecycledPage(secondPage)) {
                        page.setSecondTexture(secondPage);
                    }
                }
            }
            // in backward flip, check first texture of first page is valid
            else if (!page.isFirstTextureSet()) {
                Bitmap firstPage = getPageView(--pageNo);
                if (!isRecycledPage(firstPage)) {
                    page.setFirstTexture(firstPage);
                }
            }

            // draw frame for page flip
            pageFlip.drawFlipFrame();
        }
        // draw stationary page without flipping
        else if (drawCommand == DRAW_FULL_PAGE) {
            if (!page.isFirstTextureSet()) {
                Bitmap firstPage = getPageView(pageNo);
                if (!isRecycledPage(firstPage)) {
                    page.setFirstTexture(firstPage);
                }
            }

            pageFlip.drawPageFrame();
        }
    }

    private boolean isRecycledPage(final Bitmap page) {
        return page == null || page.isRecycled();
    }

    /**
     * Handle GL surface is changed
     *
     * @param width surface width
     * @param height surface height
     */
    public void onSurfaceChanged(int width, int height) {
    }

    /**
     * Handle ended drawing event
     * In here, we only tackle the animation drawing event, If we need to
     * continue requesting render, please return true. Remember this function
     * will be called in main thread
     *
     * @param what event type
     * @return ture if need render again
     */
    public boolean onEndedDrawing(int what) {
        if (what == DRAW_ANIMATING_FRAME) {
            boolean isAnimating = pageFlip.animating();
            // continue animating
            if (isAnimating) {
                drawCommand = DRAW_ANIMATING_FRAME;
                return true;
            }
            // animation is finished
            else {
                final PageFlipState state = pageFlip.getFlipState();
                // update page number for backward flip
                if (state == PageFlipState.END_WITH_BACKWARD) {
                    // don't do anything on page number since pageNo is always
                    // represents the FIRST_TEXTURE no;
                }
                // update page number and switch textures for forward flip
                else if (state == PageFlipState.END_WITH_FORWARD) {
                    pageFlip.getFirstPage().setFirstTextureWithSecond();
                    pageNo++;
                }

                drawCommand = DRAW_FULL_PAGE;
                return true;
            }
        }
        return false;
    }

    /**
     * If page can flip forward
     *
     * @return true if it can flip forward
     */
    public boolean canFlipForward() {
        return (pageNo < pageCount);
    }

    /**
     * If page can flip backward
     *
     * @return true if it can flip backward
     */
    public boolean canFlipBackward() {
        if (pageNo > 0) {
            pageFlip.getFirstPage().setSecondTextureWithFirst();
            return true;
        }
        else {
            return false;
        }
    }
}
