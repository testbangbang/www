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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.eschao.android.widget.pageflip.GLProgram;
import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipException;
import com.onyx.phone.reader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.onyx.phone.reader.reader.opengl.PageRender.DRAW_FULL_PAGE;

/**
 * Page flip view
 *
 * @author eschao
 */

public class PageRenderView extends GLSurfaceView implements Renderer, GestureDetector.OnGestureListener {

    private final static String TAG = "PageFlipView";

    private int pageNo;
    private int duration;
    private PageFlip pageFlip;
    private PageRender pageRender;
    private ReentrantLock drawLock;
    private GestureDetector gestureDetector;
    private PageProvider pageProvider;
    private SizeChangedListener sizeChangedListener;
    private ViewChangedOListener viewChangedOListener;
    private boolean enableGesture = true;
    private List<IOpenGLObject> shapeObjects = new ArrayList<>();

    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private GLProgram vertexProgram = new GLProgram();

    public PageRenderView(Context context) {
        super(context);
        init(context);
    }

    public PageRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        // load preferences
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        duration = pref.getInt(Constants.PREF_DURATION, 1000);
        int pixelsOfMesh = pref.getInt(Constants.PREF_MESH_PIXELS, 10);
        boolean isAuto = pref.getBoolean(Constants.PREF_PAGE_MODE, true);

        // create PageFlip
        pageFlip = new PageFlip(context);
        pageFlip.setSemiPerimeterRatio(0.8f)
                .setShadowWidthOfFoldEdges(5, 60, 0.3f)
                .setShadowWidthOfFoldBase(5, 80, 0.4f)
                .setPixelsOfMesh(pixelsOfMesh)
                .enableAutoPage(isAuto);
        setEGLContextClientVersion(2);

        // init others
        pageNo = 0;
        drawLock = new ReentrantLock();
        pageRender = new SinglePageRender(context, pageFlip, pageNo);
        // configure render
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        gestureDetector = new GestureDetector(context, this);
    }

    public void updateShapeObjects(List<IOpenGLObject> openGLObjects) {
        this.shapeObjects = openGLObjects;
        requestRender();
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    public void setViewChangedOListener(ViewChangedOListener viewChangedOListener) {
        this.viewChangedOListener = viewChangedOListener;
    }

    public void setSizeChangedListener(SizeChangedListener sizeChangedListener) {
        this.sizeChangedListener = sizeChangedListener;
    }

    public void setEnableGesture(boolean enableGesture) {
        this.enableGesture = enableGesture;
    }

    /**
     * Is auto page mode enabled?
     *
     * @return true if auto page mode enabled
     */
    public boolean isAutoPageEnabled() {
        return pageFlip.isAutoPageEnabled();
    }

    /**
     * Enable/Disable auto page mode
     *
     * @param enable true is enable
     */
    public void enableAutoPage(boolean enable) {
        if (pageFlip.enableAutoPage(enable)) {
            try {
                drawLock.lock();
                pageRender = new SinglePageRender(getContext(),
                        pageFlip,
                        pageNo);
                pageRender.onSurfaceChanged(pageFlip.getSurfaceWidth(),
                        pageFlip.getSurfaceHeight());
                requestRender();
            }
            finally {
                drawLock.unlock();
            }
        }
    }

    /**
     * Get duration of animating
     *
     * @return duration of animating
     */
    public int getAnimateDuration() {
        return duration;
    }

    /**
     * Set animate duration
     *
     * @param duration duration of animating
     */
    public void setAnimateDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get pixels of mesh
     *
     * @return pixels of mesh
     */
    public int getPixelsOfMesh() {
        return pageFlip.getPixelsOfMesh();
    }

    /**
     * Handle finger down event
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerDown(float x, float y) {
        // if the animation is going, we should ignore this event to avoid
        // mess drawing on screen
        updatePage(pageNo - 1);
        updatePage(pageNo + 1);
        if (!pageFlip.isAnimating() &&
            pageFlip.getFirstPage() != null) {
            pageFlip.onFingerDown(x, y);
        }
    }

    /**
     * Handle finger moving event
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerMove(float x, float y) {
        if (pageFlip.isAnimating()) {
            // nothing to do during animating
        }
        else if (pageFlip.canAnimate(x, y)) {
            // if the point is out of current page, try to start animating
            onFingerUp(x, y);
        }
        // move page by finger
        else if (pageFlip.onFingerMove(x, y)) {
            try {
                drawLock.lock();
                if (pageRender != null &&
                    pageRender.onFingerMove(x, y)) {
                    requestRender();
                }
            }
            finally {
                drawLock.unlock();
            }
        }
    }

    /**
     * Handle finger up event and start animating if need
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerUp(float x, float y) {
        if (!pageFlip.isAnimating()) {
            pageFlip.onFingerUp(x, y, duration);
            try {
                drawLock.lock();
                if (pageRender != null &&
                    pageRender.onFingerUp(x, y)) {
                    requestRender();
                }
            }
            finally {
                drawLock.unlock();
            }
        }
    }

    /**
     * Draw frame
     *
     * @param gl OpenGL handle
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            drawLock.lock();
            if (pageRender != null) {
                pageRender.onDrawFrame();
                for (IOpenGLObject openGLObject : shapeObjects) {
                    openGLObject.draw(gl, mvpMatrix, vertexProgram.getProgramRef());
                }
                if (pageRender.onEndedDrawing(pageRender.drawCommand)) {
                    if (viewChangedOListener != null && pageRender.drawCommand == DRAW_FULL_PAGE) {
                        viewChangedOListener.onViewChanged(pageRender.getPageNo(), pageRender.forward);
                    }
                    requestRender();
                }
            }
        }
        finally {
            drawLock.unlock();
        }
    }

    /**
     * Handle surface is changed
     *
     * @param gl OpenGL handle
     * @param width new width of surface
     * @param height new height of surface
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            pageFlip.onSurfaceChanged(width, height);

            int pageNo = pageRender.getPageNo();
            // if there is only one page, create single page render when need
            if(!(pageRender instanceof SinglePageRender)) {
                pageRender.release();
                pageRender = new SinglePageRender(getContext(),
                        pageFlip,
                        pageNo);
            }

            // let page render handle surface change
            pageRender.onSurfaceChanged(width, height);
            if (sizeChangedListener != null) {
                sizeChangedListener.onSizeChanged(width, height);
            }
            updateMatrix();
        }
        catch (PageFlipException e) {
            Log.e(TAG, "Failed to run PageFlipFlipRender:onSurfaceChanged");
        }
    }

    private void updateMatrix() {
        Matrix.frustumM(projectionMatrix, 0, 1, -1, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    /**
     * Handle surface is created
     *
     * @param gl OpenGL handle
     * @param config EGLConfig object
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            pageFlip.onSurfaceCreated();
            vertexProgram.init(getContext(), R.raw.shape_vertex_shader, R.raw.shape_fragment_shader);
        }
        catch (PageFlipException e) {
            Log.e(TAG, "Failed to run PageFlipFlipRender:onSurfaceCreated");
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (enableGesture) {
            onFingerDown(e.getX(), e.getY());
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (enableGesture) {
            onFingerMove(e2.getX(), e2.getY());
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onTouchEvent(View view, MotionEvent event) {
        boolean touch = gestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onFingerUp(event.getX(), event.getY());
            return true;
        }

        return touch;
    }

    public void setCurrentIndex(final int position) {
        pageNo = position;
        updatePage(position);
        requestRender();
    }

    private void updatePage(int position) {
        if (pageProvider != null) {
            Bitmap page = pageProvider.getPageView(position);
            if (page != null) {
                pageRender.updatePage(page, position);
            }
            pageRender.setPageCount(pageProvider.getPageCount());
        }
    }

    public interface PageProvider {
        int getPageCount();
        Bitmap getPageView(int position);
    }
    public interface SizeChangedListener {
        void onSizeChanged(int width, int height);
    }

    public interface ViewChangedOListener {
        void onViewChanged(int position, boolean next);
    }
}
