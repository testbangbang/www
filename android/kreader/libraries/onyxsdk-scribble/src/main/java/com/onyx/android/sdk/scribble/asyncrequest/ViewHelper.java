package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by lxm on 2017/8/15.
 */

public class ViewHelper {

    private SurfaceView hostView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private NoteManager parent;

    public ViewHelper(NoteManager noteManager) {
        parent = noteManager;
    }

    public void setHostView(SurfaceView hostView) {
        this.hostView = hostView;
        initView(hostView);
    }

    public SurfaceView getHostView() {
        return hostView;
    }

    private void initView(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(getGlobalLayoutListener());
    }

    public void resetView() {
        parent.getPenManager().pauseDrawing();
        parent.getPenManager().enableScreenPost(true);
    }

    private ViewTreeObserver.OnGlobalLayoutListener getGlobalLayoutListener() {
        if (globalLayoutListener == null) {
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateViewMatrix();
                    updateLimitRect();
                }
            };
        }
        return globalLayoutListener;
    }

    private void updateViewMatrix() {

    }

    private void updateLimitRect() {

    }

    public Rect getViewportSize() {
        if (hostView != null) {
            return new Rect(0, 0, hostView.getWidth(), hostView.getHeight());
        }
        return null;
    }

    public RectF getViewportSizeF() {
        if (hostView != null) {
            return new RectF(0, 0, hostView.getWidth(), hostView.getHeight());
        }
        return null;
    }

    public void quit() {
        if (globalLayoutListener == null) {
            return;
        }
        hostView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        hostView = null;
        globalLayoutListener = null;
    }
}
