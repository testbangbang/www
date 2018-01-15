package com.onyx.android.sdk.pen;

import android.graphics.Rect;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {

    public static final int STROKE_STYLE_PENCIL = EpdPenManager.STROKE_STYLE_PENCIL;
    public static final int STROKE_STYLE_BRUSH = EpdPenManager.STROKE_STYLE_BRUSH;

    private class ReaderCallback extends RawInputCallback {

        RawInputCallback callback;

        public ReaderCallback(RawInputCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onBeginRawDrawing(final boolean shortcutDrawing, final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onBeginRawDrawing(shortcutDrawing, point);
                }
            });
        }

        @Override
        public void onEndRawDrawing(final boolean outLimitRegion, final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onEndRawDrawing(outLimitRegion, point);
                }
            });
        }

        @Override
        public void onRawDrawingTouchPointMoveReceived(final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRawDrawingTouchPointMoveReceived(point);
                }
            });
        }

        @Override
        public void onRawDrawingTouchPointListReceived(final TouchPointList pointList) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRawDrawingTouchPointListReceived(pointList);
                }
            });
        }

        @Override
        public void onBeginRawErasing(final boolean shortcutErasing, final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onBeginRawErasing(shortcutErasing, point);
                }
            });
        }

        @Override
        public void onEndRawErasing(final boolean outLimitRegion, final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onEndRawErasing(outLimitRegion, point);
                }
            });
        }

        @Override
        public void onRawErasingTouchPointMoveReceived(final TouchPoint point) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRawErasingTouchPointMoveReceived(point);
                }
            });
        }

        @Override
        public void onRawErasingTouchPointListReceived(final TouchPointList pointList) {
            if (callback == null) {
                return;
            }
            hostView.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRawErasingTouchPointListReceived(pointList);
                }
            });
        }
    }

    private View hostView;
    private ReaderCallback callback;
    private EpdPenManager epdPenManager;
    private RawInputManager rawInputManager;

    private boolean rawDrawingCreated = false;
    private boolean rawDrawingEnabled = false;

    private TouchHelper(View view, RawInputCallback callback) {
        this.hostView = view;
        this.callback = new ReaderCallback(callback);

        setupRawInputManager(view, this.callback);
        setupEpdPenManager(view);
    }

    public static TouchHelper create(View hostView, RawInputCallback callback) {
        if (hostView == null) {
            throw new IllegalArgumentException("hostView should not be null!");
        }
        TouchHelper helper = new TouchHelper(hostView, callback);
        return helper;
    }

    public void setStrokeStyle(int style) {
        if (style == STROKE_STYLE_BRUSH) {
            getEpdPenManager().setStrokeStyle(STROKE_STYLE_BRUSH);
        } else {
            getEpdPenManager().setStrokeStyle(STROKE_STYLE_PENCIL);
        }
    }

    public TouchHelper setStrokeWidth(float w) {
        getRawInputManager().setStrokeWidth(w);
        return this;
    }

    public TouchHelper setLimitRect(Rect limitRect, List<Rect> excludeRectList) {
        getRawInputManager().setLimitRect(limitRect, excludeRectList);
        return this;
    }

    public TouchHelper setLimitRect(List<Rect> limitRectList, List<Rect> excludeRectList) {
        getRawInputManager().setLimitRect(limitRectList, excludeRectList);
        return this;
    }

    public TouchHelper openRawDrawing() {
        createRawDrawing();
        rawDrawingCreated = true;
        return this;
    }

    public void closeRawDrawing() {
        rawDrawingCreated = false;
        pauseRawDrawing();
        destroyRawDrawing();
    }

    public TouchHelper setRawDrawingEnabled(boolean enabled) {
        if (!rawDrawingCreated) {
            return this;
        }
        if (enabled) {
            resumeRawDrawing();
        } else {
            pauseRawDrawing();
        }
        rawDrawingEnabled = enabled;
        return this;
    }

    public TouchHelper setRawDrawingRenderEnabled(boolean enabled) {
        if (!rawDrawingEnabled) {
            return this;
        }
        if (enabled) {
            resumeRawDrawingRender();
        } else {
            pauseRawDrawingRender();
        }
        return this;
    }

    private EpdPenManager getEpdPenManager() {
        if (epdPenManager == null) {
            epdPenManager = new EpdPenManager();
        }
        return epdPenManager;
    }

    private void setupEpdPenManager(final View view) {
        getEpdPenManager().setHostView(view);
    }

    private void setupRawInputManager(final View view, ReaderCallback callback) {
        getRawInputManager().setHostView(view);
        getRawInputManager().setRawInputCallback(callback);
    }

    private RawInputManager getRawInputManager() {
        if (rawInputManager == null) {
            rawInputManager = new RawInputManager();
        }
        return rawInputManager;
    }

    private void createRawDrawing() {
        getRawInputManager().startRawInputReader();
        getEpdPenManager().startDrawing();
    }

    private void destroyRawDrawing() {
        getRawInputManager().quitRawInputReader();
        getEpdPenManager().quitDrawing();
    }

    private void pauseRawDrawing() {
        EpdController.leaveScribbleMode(getRawInputManager().getHostView());
        getRawInputManager().pauseRawInputReader();
        getEpdPenManager().pauseDrawing();
    }

    private void resumeRawDrawing() {
        getRawInputManager().resumeRawInputReader();
        getEpdPenManager().resumeDrawing();
    }

    private void pauseRawDrawingRender() {
        EpdController.leaveScribbleMode(getRawInputManager().getHostView());
        getEpdPenManager().pauseDrawing();
    }

    private void resumeRawDrawingRender() {
        getEpdPenManager().resumeDrawing();
    }

}

