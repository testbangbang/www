package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginShapeSelectEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchDownEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchMoveEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchUpEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectingEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class ViewHelper {

    private View hostView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private NoteManager parent;

    public ViewHelper(NoteManager noteManager) {
        parent = noteManager;
    }

    public void setHostView(View hostView) {
        this.hostView = hostView;
        initView(hostView);
    }

    public View getHostView() {
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
        globalLayoutListener = null;
    }
}
