package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.onyx.android.sdk.api.device.epd.EpdController;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class ViewHelper {

    private SurfaceView hostView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private EventBus eventBus;
    private NoteManager noteManager;

    public ViewHelper(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setHostView(NoteManager noteManager, SurfaceView hostView) {
        this.hostView = hostView;
        this.noteManager = noteManager;
        initView(hostView);
    }

    public SurfaceView getHostView() {
        return hostView;
    }

    private void initView(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(getGlobalLayoutListener());
    }

    private ViewTreeObserver.OnGlobalLayoutListener getGlobalLayoutListener() {
        if (globalLayoutListener == null) {
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateLimitRect();
                }
            };
        }
        return globalLayoutListener;
    }

    private void updateLimitRect() {
        noteManager.getTouchHelper().setup(hostView);
        EpdController.setScreenHandWritingRegionLimit(hostView);
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
